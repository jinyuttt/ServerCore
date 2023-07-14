package managerfile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 文件
 */

public class FileUtil {
    private Log log = LogFactory.getLog(FileUtil.class);
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
