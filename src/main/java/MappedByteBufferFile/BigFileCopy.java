package MappedByteBufferFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class BigFileCopy {
    public static void copyByChannelToChannel(String source, String dest) throws IOException {
        File source_tmp_file = new File(source);

        if (!source_tmp_file.exists()) {
            return ;

        }

        RandomAccessFile source_file = new RandomAccessFile(source_tmp_file, "r");

        FileChannel source_channel = source_file.getChannel();

        File dest_tmp_file = new File(dest);

        if (!dest_tmp_file.isFile()) {
            if (!dest_tmp_file.createNewFile()) {
                source_channel.close();

                source_file.close();

                return;

            }

        }

        RandomAccessFile dest_file = new RandomAccessFile(dest_tmp_file, "rw");

        FileChannel dest_channel = dest_file.getChannel();

        long left_size = source_channel.size();

        long position = 0;

        while (left_size > 0) {
            long write_size = source_channel.transferTo(position, left_size, dest_channel);

            position += write_size;

            left_size -= write_size;

        }

        source_channel.close();

        source_file.close();

        dest_channel.close();

        dest_file.close();

    }

    public static void main(String[] args) {
        try {
            long start_time = System.currentTimeMillis();

            BigFileCopy.copyByChannelToChannel("source_file", "dest_file");

            long end_time = System.currentTimeMillis();

            System.out.println("copy time = " + (end_time - start_time));

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
