package DB;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.concurrent.ConcurrentHashMap;

public class DBManager {

    private  static  DBManager obj=new DBManager();

    private ConcurrentHashMap<String,HikariDataSource> map=new ConcurrentHashMap<>();
    public   static    DBManager getInstance() {
        return obj;
    }
    HikariDataSource ds=null;
    private void init()
    {
        HikariConfig config = new HikariConfig("/hikari.properties");
        ds = new HikariDataSource(config);
    }
    private void init(String name)
    {
        String path="/"+name+".properties";
        HikariConfig config = new HikariConfig(path);
        var tmp = new HikariDataSource(config);
        map.put(name,tmp);
    }
    public HikariDataSource getDs(String name) {
        if (name == null || name.isEmpty()) {
            if (ds == null) {
                init();
            }
            return ds;
        } else {
            var tmp = map.getOrDefault(name, null);
            if (tmp == null) {
                init(name);
                tmp = map.getOrDefault(name, null);

            }
            return tmp;
        }
    }
}
