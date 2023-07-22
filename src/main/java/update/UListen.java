package update;

import managerfile.FileUtil;
import net.Zmqnet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;

/**
 * 监听外部通知立即升级
 */
public class UListen {
    static  String updateAddress="";

    static  String topic="";
    private static Log log = LogFactory.getLog(UListen.class);
    public void  start()
    {
        Zmqnet.initSubaddress(updateAddress);
        Zmqnet.subscribe(topic);
        Thread  rec=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        var msg = Zmqnet.getMsg();
                        //立即检查
                        SoftCheck.getInstance().getDB(msg.toString());
                    }
                    } catch(InterruptedException e){
                        log.error(e);
                    } catch (SQLException e) {
                     log.error(e);
                }


            }
        });
        rec.setDaemon(true);
        rec.setName("updatelisten");
        rec.start();

    }
}
