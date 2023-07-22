package net;


/**
 * 订阅接收的数据
 */
public class TopicMsg {

    /**
     * 主题
     */
    public  String  topic;

    /**
     * 数据
     */
    public  byte[] data;

    /**
     * 数据字符串
     * @return
     */
    @Override
    public String toString() {
         if (data==null)
         {
             return  null;
         }
         else
         {
             return  new String(data);
         }
    }
}
