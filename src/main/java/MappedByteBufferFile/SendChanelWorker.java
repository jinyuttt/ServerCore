package MappedByteBufferFile;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SendChanelWorker extends Thread implements IWorker {
    private final SocketChannel socket;
    private final String fpath;
    public SendChanelWorker(SocketChannel socket, String file) {
        this.socket = socket;
        fpath = file;
    }

    public void run() {

        transform();
    }

    private  void  transform()
    {
        try {
            MMapBufferChanel mMampBuffer = new MMapBufferChanel();
            mMampBuffer.readFile(fpath);
            ByteBuffer buffer = null;
            long start=System.currentTimeMillis();
            do {
                buffer = mMampBuffer.read();
                while (buffer.hasRemaining()) {
                   socket.write(buffer);
                    System.out.println("发送");
                }
            } while (!mMampBuffer.getRead());
            socket.close();
            mMampBuffer.close();
            System.out.println("传输完成");
            System.out.println((System.currentTimeMillis()-start)/1000+"秒");
            if(channelNotify!=null)
            {
                channelNotify.sendMsg(this);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


   IChannelNotify channelNotify;
    @Override
    public void setChannel(IChannelNotify notify) {
        channelNotify=notify;
    }
}
