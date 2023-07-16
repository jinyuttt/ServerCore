package net;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;

public class FtpServer {
    private static final int DEF_FTP_PORT = 2323;

    public void  start() throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(DEF_FTP_PORT);

// replace the default listener
        serverFactory.addListener("default", factory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File("users.properties"));
        serverFactory.setUserManager(userManagerFactory.createUserManager());


// start the server

        var server = serverFactory.createServer();

        server.start();

    }
}
