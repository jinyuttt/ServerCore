package managerfile;

/**
 * 配置
 */
public class AppclitionConfig {



    /**
     * 文件管理策略（1同时执行存储容量占比和时间，2,容量占比，3存储时间）
     */
    public static int storePlocy=1;

    /**
     * 存储占比
     */

    public static double storeUsag=80;


    /**
     * 删除到最小占比
     */
    public  static double storeMin=60;

    /**
     * 存储时间（小时）
     */
    public static  int   storetime=12;

    /**
     * 文件根目录
     */

    public static  String dirPath="";


    /**
     * 存储占比时是否继续删除
     */
    public  static  boolean isStore=false;
}
