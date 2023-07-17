package net;

import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class FtpClient {
    /**
     * ftp上传单个文件
     *
     * @param ftpUrl      ftp地址
     * @param userName    ftp的用户名
     * @param password    ftp的密码
     * @param directory   上传至ftp的路径名不包括ftp地址
     * @param srcFileName 要上传的文件全路径名
     * @param destName    上传至ftp后存储的文件名
     * @throws IOException
     */
    public static boolean upload(String logSeq, String ftpUrl, String userName, int port,
                                 String password, String directory, String srcFileName, String destName) throws IOException {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;
        boolean result = false;
        try {
            ftpClient.connect(ftpUrl, port);
            ftpClient.login(userName, password);
            ftpClient.enterLocalPassiveMode();
            File srcFile = new File(srcFileName);
            fis = new FileInputStream(srcFile);
// 设置上传目录
            ftpClient.changeWorkingDirectory(directory);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("gbk");
// 设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            result = ftpClient.storeFile(destName, fis);
            return result;
        } catch (NumberFormatException e) {
            System.out.println("FTP端口配置错误:不是数字:");
            throw e;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {

            fis.close();
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
    }


    /**
     * FTP单个文件下载
     *
     * @param ftpUrl       ftp地址
     * @param userName     ftp的用户名
     * @param password     ftp的密码
     * @param directory    要下载的文件所在ftp的路径名不包含ftp地址
     * @param destFileName 要下载的文件名
     * @param downloadName 下载后锁存储的文件名全路径
     */
    public static boolean download(String logSeq, String ftpUrl, String userName, int port,
                                   String password, String directory, String destFileName, String downloadName) throws IOException {
        FTPClient ftpClient = new FTPClient();
        boolean result = false;
        try {
            ftpClient.connect(ftpUrl, port);
            ftpClient.login(userName, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(1024);
// 设置文件类型（二进制）
            ftpClient.changeWorkingDirectory(directory);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            System.out.println("destFileName:" + destFileName + ",downloadName:" + downloadName);
            result = ftpClient.retrieveFile(destFileName, new FileOutputStream(downloadName));
            return result;
        } catch (NumberFormatException e) {
            throw e;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
    }

    /**
     * @param ftpUrl    ftp地址
     * @param userName  ftp的用户名
     * @param password  ftp的密码
     * @param directory 要删除的文件所在ftp的路径名不包含ftp地址
     * @param fileName  要删除的文件名
     * @return
     * @throws IOException
     */
    public static boolean remove(String logSeq, String ftpUrl, String userName, int port,
                                 String password, String directory, String fileName) throws IOException {
/**
 * 判断远程文件是否移除成功，如果成功返回true，否则返回false
 */
        boolean result = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpUrl, port);
            ftpClient.login(userName, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(directory);
            result = ftpClient.deleteFile(fileName);//删除远程文件
            return result;
        } catch (NumberFormatException e) {
            throw e;
        } catch (IOException e) {
            throw new IOException("连接ftp服务器失败！", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
    }

    /**
     * @param ftpUrl       ftp地址
     * @param userName     ftp的用户名
     * @param password     ftp的密码
     * @param directory    要创建的目录所在ftp的路径名不包含ftp地址
     * @param newDirectory 要创建的新目录名
     * @return
     * @throws IOException
     */
    public static boolean makeDirecotory(String logSeq, String ftpUrl, String userName, int port,
                                         String password, String directory, String newDirectory) throws IOException {
/**
 * 判断远程文件是否移除成功，如果成功返回true，否则返回false
 */
        boolean result = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpUrl, port);
            ftpClient.login(userName, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(directory);
            result = ftpClient.makeDirectory(newDirectory);//创建新目录
            return result;
        } catch (NumberFormatException e) {
            throw e;
        } catch (IOException e) {
            throw new IOException("连接ftp服务器失败！", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
    }
}
