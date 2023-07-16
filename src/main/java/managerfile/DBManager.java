package managerfile;

import com.google.gson.Gson;

/**
 * 读取数据库
 */
public class DBManager {

    /**
     * 读取文件阿时间保存策略（小时、天、周、月、半年、季度、年）名字需要与策略名称一致
     * @return
     */
    public  String  readDB()
    {
        return  "";
    }

    /**
     * 读取具体时间（小时、天、周、月三种策略需要设置值）
     */
    public String getDB()
    {
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
