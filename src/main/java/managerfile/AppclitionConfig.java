package managerfile;

public class AppclitionConfig {

    /**
     * 是否使用数据库
     */
    public static   boolean readDB=false;

    /**
     * 文件管理策略（1同时执行存储容量占比和时间，2,容量占比，3存储时间）
     */
    public static int polcy=1;

    /**
     * 存储占比
     */

    public static double storeUsag=80;

    /**
     * 存储时间（小时）
     */
    public static  int   storetime=12;

    /**
     * 文件根目录
     */

    public static  String dirPath="";
}
