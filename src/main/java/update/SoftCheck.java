package update;

import DB.DBManager;
import DB.H2Util;
import compress.ZFileUtil;
import managerfile.FileUtil;
import net.FtpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * 软件升级
 */
public class SoftCheck {
    private Log log = LogFactory.getLog(FileUtil.class);

    private List<String> lst=new ArrayList<>();

    private String dir="";

    private  static  final  SoftCheck obj=new SoftCheck();

    public   static  final  SoftCheck getInstance()
    {
            return  obj;
    }
    /**
     * 定时更新

     * @param delay
     */
    public  void  start(int delay) {
        Timer timer = new Timer();
        long depaly=1000*60L;
        long perid=1000*60*60*12;//
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    updateCheck();
                } catch (Exception e) {
                    log.error(e);
                }
                log.info("更新");
            }
        }, delay, perid);
    }

    /**
     * 检查更新
     */
    private void updateCheck() {
        try {
            for (String name:lst
                 ) {
                getDB(name);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public   void   getDB(String name) throws SQLException {
        try (var statement = DBManager.getInstance().getDs(null).getConnection().createStatement()) {
            try {
                String sql=String.format("select  url,port,ver from soft where  name like '{0}'",name);
                var view = statement.executeQuery(sql);
                String ver = view.getString("ver");
                String url=view.getString("url");
                int port=view.getInt("port");
                var h2 = H2Util.getConnect().createStatement().executeQuery(sql);
                var cur = h2.getString("ver");
                if (ver != cur) {
                    //2个版本不等
                    //检查是否有大版更新。
                    //查询表中是否只是一个文件，并且是压缩包。
                    String mysql=String.format("select file,md  from  soft m,version n where m.id=n.softid and name='{0}'",name);
                    var c = statement.executeQuery(mysql);
                    int num = c.getRow();
                    if (num == 1) {
                        String file = c.getString("file");
                        String md5 = c.getString("md");
                        if (file.endsWith(".zip")) {
                            //说明是压缩包。则下载直接解压；
                            FtpClient.download("", url, "root", port, "root", dir, file, file);
                            StringBuffer lstPath=new StringBuffer();
                            ZFileUtil.unCompressedFiles(file, dir, null,lstPath);//解压
                            if(!lstPath.isEmpty()) {
                                String[] files=lstPath.toString().split(";");
                                for (String f:files
                                     ) {
                                    //计算MD5；
                                    String mdtmp=ZFileUtil.HexMd5(f);
                                    String sqlt = String.format("insert into version(file,version,md,name) values({0},{1},{2},{3} ", f, ver, mdtmp, name);
                                    H2Util.getConnect().createStatement().execute(sqlt);
                                }

                            }
                        }
                        else
                        {
                            //一个文件

                            //直接下载目录
                            FtpClient.download("", url, "root", port, "root", dir, file, file);
                            //同时入库本地
                            String sqlt = String.format("insert into version(file,version,md,name) values({0},{1},{2},{3} ", file, ver, md5, name);
                            H2Util.getConnect().createStatement().execute(sqlt);
                        }
                    } else {
                        // 
                       String query=String.format("select md  from  soft m,version n where m.id=n.softid  name='{0}'",name);
                        var rs = H2Util.getConnect().createStatement().executeQuery(mysql);
                        StringJoiner stringJoiner = new StringJoiner(",");
                        int rcount = rs.getRow();
                        for (int i = 0; i < rcount; i++) {
                            stringJoiner.add(rs.getString("md"));
                        }

                        //
                        String querySql=String.format("select file,md from version where name like '{0}' and md in({1})",name,stringJoiner);
                        var drs = statement.executeQuery(querySql);
                        rcount = drs.getRow();
                        for (int i = 0; i < rcount; i++) {
                            String patth = drs.getString("file");
                            String md5 = drs.getString("md");

                            //直接下载目录
                            FtpClient.download("", url, "root", port, "root", dir, patth, patth);
                            //同时入库本地
                            String sqlt = String.format("insert into version(file,version,md,name) values({0},{1},{2},{3} ", patth, ver, md5, name);
                            H2Util.getConnect().createStatement().execute(sqlt);
                        }

                    }


                }

            } catch (Exception e) {
               log.error(e);
            }
        }
    }
}
