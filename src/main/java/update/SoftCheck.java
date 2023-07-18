package update;

import DB.DBManager;
import DB.H2Util;
import compress.ZFileUtil;
import managerfile.FileUtil;
import net.FtpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

public class SoftCheck {
    private Log log = LogFactory.getLog(FileUtil.class);
    /**
     * 定时更新
     * @param time
     * @param delay
     */
    public  void  start(int time,int delay) {
        Timer timer = new Timer();
        long depaly=1000*60L;
        long perid=1000*60*60*12;
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
            getDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private  void   getDB() throws SQLException {
        try (var statement = DBManager.getInstance().getDs(null).getConnection().createStatement()) {
            try {
                var view = statement.executeQuery("select  v from vison where  name= like 'test'");
                String ver = view.getString("ver");
                var h2 = H2Util.getConnect().createStatement().executeQuery("select  v from vison where  name= like 'test'");
                var cur = h2.getString("ver");
                if (ver != cur) {
                    //2个版本不等
                    //检查是否有大版更新。
                    //查询表中是否只是一个文件，并且是压缩包。
                    var c = statement.executeQuery("select v num from  version where name='test'");
                    int num = c.getRow();
                    if (num == 1) {
                        String file = c.getString(0);
                        if (file.endsWith(".zip")) {
                            //说明是压缩包。则下载直接解压；
                            FtpClient.download("", "192.168.10.12", "root", 21, "root", "ftp", file, file);
                            ZFileUtil.unCompressedFilesToSameDir(file, file, null);//解压

                        }
                    } else {
                        //查询需要更新的文件
                        var rs = H2Util.getConnect().createStatement().executeQuery("select md from version where  name like 'test'");
                        StringJoiner stringJoiner = new StringJoiner(",");
                        int rcount = rs.getRow();
                        for (int i = 0; i < rcount; i++) {
                            stringJoiner.add(rs.getString(0));
                        }
                        var drs = statement.executeQuery("select file from version where name like 'test' and md in" + stringJoiner.toString() + ")");
                        rcount = drs.getRow();
                        for (int i = 0; i < rcount; i++) {
                            String patth = drs.getString(0);
                            String md5 = drs.getString(1);
                            //直接下载目录
                            FtpClient.download("", "192.168.10.12", "root", 21, "root", "ftp", patth, patth);
                            //同时入库本地
                            String sql = String.format("insert into version(file,version,md,name) values({0},{1},{2},{3} ", patth, ver, md5, "tes");
                            H2Util.getConnect().createStatement().execute(sql);
                        }

                    }


                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
