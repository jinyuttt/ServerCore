package MappedByteBufferFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * 文件读写
 */
public class MMapBufferChanel {

    /**
     * 读写缓存
     */
    private final int size = 4096 * 1024 * 250;


    public int getSize() {
        return size;
    }

    private RandomAccessFile randomAccessFile = null;

    /**
     * 读取次数
     */
    private long readNum = 0;

   private  boolean isRead=false;
    /**
     * 写入的文件
     */
    private String writeFile;

    /**
     * 写入的
     */
    private MappedByteBuffer writeMap = null;

    /**
     * 已经写的缓存
     */
    private  long foceLen=0;

    /**
     * 写的文件长度
     */
    private long fileLen;

    /**
     * 已经写入的长度
     */
    private long writeLen = 0;

    /**
     * 读取完成
     * @return
     */
    public boolean getRead()
    {
        return  isRead;
    }

    public void setFileLen(long len) {
        this.fileLen = len;
    }


    public void setWriteFile(String file) {
        this.writeFile = file;
    }
   public  void  close() throws IOException {
       if(randomAccessFile!=null)
       {
           randomAccessFile.close();
       }
   }
    /**
     * 写入
     * @param src
     * @throws IOException
     */
    public void writeFileBuffer(ByteBuffer src) throws IOException {

        if (randomAccessFile == null) {

            long curLen = fileLen - writeLen;//计算剩余的字节
            if (curLen > size) {
                curLen = size;
            }
            try {
                randomAccessFile = new RandomAccessFile(writeFile, "rw");
               // randomAccessFile.setLength(fileLen);
                FileChannel rafchannel = randomAccessFile.getChannel();
                //mmap 使得jvm堆和pageCache有一块映射空间
                MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.READ_WRITE, writeLen, curLen);  // 1000M的pageCache大小
                map.put(src);
                writeMap = map;
                writeLen += src.limit();
                foceLen=src.limit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            if (foceLen + src.limit() > size) {
                //重新加载
                long curLen = fileLen - writeLen;
                if (curLen > size) {
                    curLen = size;
                }
                MappedByteBuffer map = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, writeLen, curLen);  // 1000M的pageCache大小
                map.put(src);
                writeMap = map;
                writeLen += src.limit();
                foceLen = src.limit();
            } else {
                writeMap.put(src);
                foceLen += src.limit();
                writeLen += src.limit();
            }
        }

    }


    /**
     * 读取文件
     * @param path
     * @throws IOException
     */
    public void readFile(String path) throws IOException {
        randomAccessFile = new RandomAccessFile(path, "rw");
    }

    /**
     * 读取数据
     * @return
     * @throws IOException
     */
    public ByteBuffer read() throws IOException {

        if (randomAccessFile != null) {

            long pos = readNum * size;//已经读取的字节
            long leftNum = randomAccessFile.getChannel().size() - pos;//剩下字节
            readNum++;
            try {
                if (leftNum > size) {
                    MappedByteBuffer map = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, pos, size);  // 1000M的pageCache大小
                    return map.asReadOnlyBuffer();
                } else {
                    isRead=true;
                    MappedByteBuffer map = randomAccessFile.getChannel().map(FileChannel.MapMode.PRIVATE, pos, leftNum);  // 1000M的pageCache大小
                    randomAccessFile.close();//最后一次了
                    return map.asReadOnlyBuffer();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
