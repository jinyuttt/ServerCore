package MappedByteBufferFile;

import java.io.File;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

public class test {
    public  static  void main(String[] args) throws IOException {
        SocketChannel  socketChannel=SocketChannel.open();
       var mm= socketChannel.getOption(StandardSocketOptions.SO_SNDBUF);
      var kk=  socketChannel.getOption(StandardSocketOptions.SO_RCVBUF);
        int size=4096 * 1024 * 100;
        long nn=6L*size;
        String separator = File.separator;
        String temp="E:\\study\\FileTransfer";

        String[] ss=temp.split(separator);
        System.out.println("cd");

    }
}
