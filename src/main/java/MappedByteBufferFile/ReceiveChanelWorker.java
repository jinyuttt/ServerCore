package MappedByteBufferFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedTransferQueue;

/**
 * 数据接收
 */
public class ReceiveChanelWorker extends  Thread implements IWorker {
    private  final  int size=4096 * 1024 * 250/2;
    private final SocketChannel socket;

    private long fileLen=0;
    private  final  String loc;
    private volatile boolean isSucess=false;

    private volatile  boolean isExit=false;
    private final LinkedTransferQueue<ByteBuffer> queue=new LinkedTransferQueue<>();
    public ReceiveChanelWorker(SocketChannel socketChannel,long flen,String file)
    {
        this.socket=socketChannel;
        this.loc=file;
        this.fileLen=flen;
    }
    public  void  run() {

        try {
             MMapBufferChanel mMampBuffer = new MMapBufferChanel();
             mMampBuffer.setWriteFile(loc);
             mMampBuffer.setFileLen(fileLen);
            process(mMampBuffer);
            transform(mMampBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param mMampBuffer
     * @throws IOException
     * @throws InterruptedException
     */
    private void transform( MMapBufferChanel mMampBuffer) throws IOException, InterruptedException {

          long start=System.currentTimeMillis();
          if(socket.finishConnect()) {
            ByteBuffer byteBuffer=ByteBuffer.allocateDirect(size);
            int length=0;
            while ((length = socket.read(byteBuffer)) > 0) {
                if(byteBuffer.position()==byteBuffer.limit()) {//写满换成再写入文件

                    byteBuffer.flip();
                    queue.transfer(byteBuffer);
                    byteBuffer=ByteBuffer.allocateDirect(size);
                }
            }

            //没有接收完成
            if(byteBuffer.position()>0)
            {
                byteBuffer.flip();
                queue.transfer(byteBuffer);
            }
             isSucess=true;
             Thread.sleep(500);
             if(!isExit) {
                 //还没有退出则阻塞等待中
                 ByteBuffer buffer = ByteBuffer.allocate(0);
                 queue.transfer(buffer);
             }
        }
          System.out.println("接收"+(System.currentTimeMillis()-start)/1000+"秒");
        if(channelNotify!=null)
        {
            channelNotify.sendMsg(this);
        }
    }

    /**
     * 写入文件
     * @param mapBufferChanel
     */
    private  void process(MMapBufferChanel mapBufferChanel)
    {
        Thread p=new Thread(new Runnable() {
            @Override
            public void run() {
                int num=0;
                while (!isSucess)
                {
                    num++;
                    try {
                        ByteBuffer buffer= queue.take();
                        mapBufferChanel.writeFileBuffer(buffer);
                       // System.out.println("写入"+num);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                isExit=true;
                try {
                    mapBufferChanel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                queue.clear();
                System.out.println("完成");

            }
        });
        p.start();
    }

   IChannelNotify  channelNotify=null;
    @Override
    public void setChannel(IChannelNotify notify) {
        channelNotify=notify;
    }
}
