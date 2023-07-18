package MappedByteBufferFile;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * tcp服务端
 */
public class FileChannelServer  implements  IChannelNotify{

    private final ServerSocketChannel server;

    private  final  String dir="E:\\";

    private  final  int so_sendBuf=20*1024*1024;
    private  final  int so_recBuf=20*1024*1024;
    private List<IWorker> lst=new ArrayList<>();

    /**
     * 端口
     * @param port
     */
    public FileChannelServer(int port)
    {
        try {
            server=ServerSocketChannel.open();
            server.configureBlocking(true);
            server.setOption(StandardSocketOptions.SO_RCVBUF,so_recBuf);
            InetSocketAddress socketAddress=new InetSocketAddress(port);
           server.bind(socketAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 开始监听
     * @throws IOException
     */
    public  void  Start() throws IOException {
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            SocketChannel socket = server.accept(); //会一直阻塞，直到有客户端连接进来
            socket.setOption(StandardSocketOptions.SO_SNDBUF,so_sendBuf);
             int size= socket.read(buffer);
             buffer.flip();
             byte model=buffer.get();
             int len = buffer.getInt();
             byte[] fname = new byte[len];
             buffer.get(fname);
             String name = new String(fname);
             if(model==1)
             {
                 //接收文件
                 long filelen=buffer.getLong();
                 File f=new File(name);
                 String localFile=dir+f.getName();
                 ReceiveChanelWorker worker=new ReceiveChanelWorker(socket,filelen,localFile);
                 worker.setChannel(this);
                 lst.add(worker);
                 worker.start();

             }
            else if(model==2) {
                 //发送文件
                 String srvFile = ProcessPath(name);
                 File f=new File(srvFile);
                 buffer.clear();
                 if(f.exists())
                 {
                     buffer.putLong(f.length());
                     buffer.flip();
                     while(buffer.hasRemaining()) {
                         socket.write(buffer);
                     }

                 }
                 else
                 {
                     buffer.putLong(0);
                     socket.close();
                     continue;
                 }
                 SendChanelWorker worker=new SendChanelWorker(socket,srvFile);
                 worker.setChannel(this);
                 lst.add(worker);
                 worker.start();
             }
        }

    }

    /**
     * 按照规则查找本地文件
     * @param localfile
     * @return
     */
    private  String  ProcessPath(String localfile)
    {
        String f="";
        File ff=new File(localfile);
        f=ff.getAbsolutePath();
        //服务端本地没有文件
        if(!ff.exists())
        {
            String separator= Utils.getSeparator();
            String tmp=ff.getAbsolutePath();
            String[] tt= tmp.split(separator);
            StringBuilder builder=new StringBuilder();
            builder.append(dir);
            //添加根盘符，认为是相对路径
            for (String ttt:tt
            ) {
                builder.append(ttt);
                builder.append(separator);
            }
            f= builder.substring(0,builder.length()-separator.length());
            File fff=new File(f);
            f=fff.getAbsolutePath();
            //还是没有则替换根路径
            if(!fff.exists())
            {
                builder=new StringBuilder();
                builder.append(dir);
                for (int i=1;i<tt.length;i++)
                {
                    builder.append(tt[i]);
                }

                f=builder.toString();
                File ffff=new File(f);
                if(!ffff.exists())
                {
                    f=dir+ff.getName();
                }
            }
        }
        return  f;
    }

    /**
     * 移除任务
     * @param obj
     */
    @Override
    public void sendMsg(IWorker obj) {
        if(obj!=null)
        {
            System.out.println("服务端移除工作");
            if(lst.contains(obj))
            {
                lst.remove(obj);
            }
            System.out.println("服务端移除工作"+lst.size());
        }
    }
    public static void main(String[] args) throws IOException {

        FileChannelServer ss = new FileChannelServer(6606);
        ss.Start();

    }


}
