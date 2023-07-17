package MappedByteBufferFile;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * 文件客户端
 */
public class FileChannelClient  implements  IChannelNotify{
    private final String serverIp;

    private final int serverPort;

    private SocketChannel socket;

   public  String dir="D:\\";

   private  final  int so_sendBuf=20*1024*1024;
    private  final  int so_recBuf=20*1024*1024;

   private List<IWorker> lst=new ArrayList<>();
    public FileChannelClient(String serverIp, int serverPort) {

        this.serverIp = serverIp;
        this.serverPort = serverPort;

    }

    public void connect() throws IOException {

        if (socket == null) {
            socket=SocketChannel.open();
            socket.configureBlocking(true);//阻塞
            socket.setOption(StandardSocketOptions.SO_SNDBUF,so_sendBuf);
            socket.setOption(StandardSocketOptions.SO_RCVBUF,so_sendBuf);
            InetSocketAddress socketAddress=new InetSocketAddress(serverIp,serverPort);
            socket.connect(socketAddress);
        }

    }

    /**
     * 传输文件到服务端
     * @param file
     * @throws IOException
     * @throws InterruptedException
     */
    public void sendFile(String file) throws IOException, InterruptedException {
        byte[] buf = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        byte[] fname=file.getBytes(Charset.forName("UTF-8"));
        File f=new File(file);
        byte model=1;
        buffer.put(model);
        buffer.putInt(file.length());
        buffer.put(fname);
        buffer.putLong(f.length());
        buffer.flip();
        //发送模式、文件名长度、文件名、文件长度
        while(buffer.hasRemaining()) {
            socket.write(buffer);
        }
       SendChanelWorker sendChanelWorker=new SendChanelWorker(socket,file);
       sendChanelWorker.setChannel(this);
        lst.add(sendChanelWorker);

        sendChanelWorker.start();
    }


    /**
     * 从服务端请求文件
     * @param file
     * @throws IOException
     * @throws InterruptedException
     */
    public void recFile(String file) throws IOException, InterruptedException {

        byte[] buf = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        byte[] fname=file.getBytes(Charset.forName("UTF-8"));
        byte model=2;
        buffer.put(model);
        buffer.putInt(file.length());
        buffer.put(fname);
        buffer.flip();
        while(buffer.hasRemaining()) {
            socket.write(buffer);
        }
        buffer.clear();
        socket.read(buffer);
        buffer.flip();
        long len = buffer.getLong();
        if (len == 0) {
            System.out.println("不存在");
            socket.close();
            return;
        }
        String localf = file;
        if (!dir.isEmpty()) {
            File f=new File(file);
            localf = dir + f.getName();
        }

        ReceiveChanelWorker receiveChanelWorker=new ReceiveChanelWorker(socket,len,localf);
        receiveChanelWorker.setChannel(this);
        lst.add(receiveChanelWorker);
        receiveChanelWorker.start();
    }

    @Override
    public void sendMsg(IWorker obj) {
        if(obj!=null)
        {
            System.out.println("客户端移除工作");
            if(lst.contains(obj))
            {
                lst.remove(obj);
            }
            System.out.println("客户端移除工作:"+lst.size());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        long start=System.currentTimeMillis();
        FileChannelClient client = new FileChannelClient("127.0.0.1", 6606);
        client.connect();
        client.sendFile("E:\\soft\\vs2022.zip");
       System.out.println((System.currentTimeMillis()-start)/1000+"秒");
    }


}
