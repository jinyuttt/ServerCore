package managerfile;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * 读取数据库
 */
public class DBManager {
    private Log log = LogFactory.getLog(DBManager.class);
    HikariDataSource ds=null;
    public void init()
    {
        HikariConfig config = new HikariConfig("/hikari.properties");
         ds = new HikariDataSource(config);
    }



    /**
     * 读取文件阿时间保存策略（小时、天、周、月、半年、季度、年）名字需要与策略名称一致
     * @return
     */
    public  String  readDB()
    {
        Statement s= null;
        try {
            s = ds.getConnection().createStatement();
        } catch (SQLException e) {
            log.error(e);
        }
        try {
            return s.executeQuery(ConfigLoad.querySql).getString("");
        } catch (SQLException e) {
            log.error(e);
        }
        return  "";
    }

    /**
     * 读取具体时间（小时、天、周、月三种策略需要设置值）
     */
    public String getDB()
    {
        Statement s= null;
        try {
            s = ds.getConnection().createStatement();
        } catch (SQLException e) {
            log.error(e);
        }
        try {
            return s.executeQuery(ConfigLoad.querySqlV).getString("");
        } catch (SQLException e) {
            log.error(e);
        }
        return  "";
    }

    public void   getDBCfg()
    {
        //读取配置，直接保存字符串
        String jsn="";
        Gson gson=new Gson();
        gson.fromJson(jsn,AppclitionConfig.class);

    }
}
