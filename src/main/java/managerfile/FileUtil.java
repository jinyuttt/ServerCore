package managerfile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 文件删除
 */

public class FileUtil {
    private Log log = LogFactory.getLog(FileUtil.class);

    /**
     * 按照时间删除文件
     * @param path
     * @param calendar
     * @throws IOException
     */
    private  void  deletefile(String path,Calendar calendar) throws IOException {
        File file=new File(path);
        if(file.isDirectory()) {
            var childfile = file.list();
            for (String str : childfile
            ) {
                deletefile(str, calendar);
            }
        }
        else
        {
            Path fpath = Paths.get(file.getAbsolutePath());
            // 根据path获取文件的基本属性类
            BasicFileAttributes attrs = Files.readAttributes(fpath, BasicFileAttributes.class);
            // 从基本属性类中获取文件创建时间
            FileTime fileTime = attrs.creationTime();
            // 将文件创建时间转成毫秒
            long millis = fileTime.toMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            Date date = new Date();
            date.setTime(millis);
            if(date.before(calendar.getTime()))
            {
                file.deleteOnExit();
            }
        }
    }

    /**
     * 获取文件时间
     * @param path
     * @return
     * @throws IOException
     */
   private  Date getFile(String path) throws IOException {
       File file=new File(path);
       if(file.isFile())
       {
           Path fpath = Paths.get(file.getAbsolutePath());
           // 根据path获取文件的基本属性类
           BasicFileAttributes attrs = Files.readAttributes(fpath, BasicFileAttributes.class);
           // 从基本属性类中获取文件创建时间
           FileTime fileTime = attrs.creationTime();
           // 将文件创建时间转成毫秒
           long millis = fileTime.toMillis();
           SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
           Date date = new Date();
           date.setTime(millis);
           return  date;
       }
       else
       {
           var lst=file.list();
           for (String fpath:lst
                ) {
               getFile(fpath);
           }
       }
       return null;
   }

    /**
     * 检查删除最早时间
     * @throws IOException
     */
    private  void  checktime() throws IOException {
        Date deleteFile=getFile(AppclitionConfig.dirPath);
        if(deleteFile==null)
        {
            deleteFile=new Date();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(deleteFile);
       deletefile(AppclitionConfig.dirPath,calendar);

    }


    private  void  deletetime()
    {
        DBManager dbManager=new DBManager();
        String dir=AppclitionConfig.dirPath;
        //按照时间
        long ccur=System.currentTimeMillis();
        String  v=  dbManager.readDB();
        String str=  dbManager.getDB();
        Calendar calendar=Calendar.getInstance();
        Plocy p=  Enum.valueOf(Plocy.class,v);
        long deletelen=0;
        switch (p) {
            case Hours -> calendar.add(Calendar.HOUR, -1 * Integer.valueOf(str));
            case Day -> calendar.add(Calendar.DATE, -1 * Integer.valueOf(str));
            case Weeks -> calendar.add(Calendar.DATE, -1 * Integer.valueOf(str) * 7);
            case Month -> calendar.add(Calendar.MONTH, -1 * Integer.valueOf(str));
            case quarter -> calendar.add(Calendar.MONTH, -1 * Integer.valueOf(str) * 3);
            case HaldYrea -> calendar.add(Calendar.MONTH, -1 * Integer.valueOf(str) * 6);
            case year -> calendar.add(Calendar.YEAR, -1 * Integer.valueOf(str));
        }
        //查找文件
    }
    Timer timer=null;
    public  void  start(int time,int delay) {
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar lstcalendar=Calendar.getInstance();
               if(AppclitionConfig.readDB)
               {
                   DBManager dbManager=new DBManager();
                  String  v=  dbManager.readDB();
                  String str=  dbManager.getDB();
                   Calendar calendar=Calendar.getInstance();
                   Plocy p=  Enum.valueOf(Plocy.class,v);
                switch (p)
                {
                    case Hours -> lstcalendar.add(Calendar.HOUR,Integer.valueOf(str));
                    case Day -> lstcalendar.add(Calendar.DATE,Integer.valueOf(str));
                    case Weeks -> lstcalendar.add(Calendar.WEEK_OF_MONTH,Integer.valueOf(str));
                    case Month -> lstcalendar.add(Calendar.MONTH,Integer.valueOf(str));
                    case quarter -> lstcalendar.add(Calendar.MONTH,Integer.valueOf(str)*3);
                    case HaldYrea -> lstcalendar.add(Calendar.MONTH,6);
                    case year -> lstcalendar.add(Calendar.YEAR,1);
                }

               }
                log.info("启动文件删除");
            }
        }, 1000, 2000);
    }
}
