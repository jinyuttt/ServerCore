package net;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Zmqnet {
    static   ZContext context=null;

    /**
     * 发布
     */
    static ZMQ.Socket publisher = null;

    /**
     * 订阅
     */
    static ZMQ.Socket subscriber=null;

    /**
     * 过滤地址
     */
    static List<String> lstAddress=new ArrayList<>();

    /**
     * 是否初始化
     */
    static  boolean isInit=true;
    static BlockingQueue<TopicMsg> queue=new ArrayBlockingQueue<TopicMsg>(10000) ;

    static
    {
         context = new ZContext();
    }

    /**
     * 本地发布地址
     */
  static  String localaddress="tcp://*:5555";


    /**
     * 发布信息
     * @param topic 主题
     * @param data 数据
     */
    public  static void  push(String topic,byte[]data) {
        if (publisher == null) {
            publisher = context.createSocket(ZMQ.PUB); //publish类型
            publisher.bind(localaddress);
        }
        publisher.sendMore(topic);
        publisher.send(data);
    }


    /**
     * 初始化订阅地址
     * @param address
     */
    public static  void  initSubaddress(String address)
    {
        if(lstAddress.contains(address))
        {
            return;
        }
        else
        {
            lstAddress.add(address);
        }
        if (subscriber == null) {
            subscriber = context.createSocket(SocketType.SUB);
        }
        if(!address.toLowerCase().startsWith("tcp"))
        {
            address="tcp://"+address;
        }
        subscriber.connect(address);
    }

    /**
     * 初始化地址
     * @param address
     */
    public static  void  initSubaddress(String[] address)
    {
       if(address!=null)
       {
           for (String addr:address
                ) {
               initSubaddress(addr);
           }
       }
    }


    /**
     * 获取订阅的数据，没有数据时会阻塞
     * @return
     * @throws InterruptedException
     */
    public  static  TopicMsg getMsg() throws InterruptedException {
        return  queue.take();
    }

    /**
     * 订阅主题，订阅时先初始化地址
     * @param topic
     */
    public static  void  subscribe(String topic) {
        if (subscriber == null) {
            subscriber = context.createSocket(SocketType.SUB);
        }
        subscriber.subscribe(topic);
        if(isInit)
        {
            isInit=false;
            Thread sub=new Thread(new Runnable() {
                @Override
                public void run() {
                while (true)
                {
                  String topic=  subscriber.recvStr();
                  byte[] data=subscriber.recv();
                  TopicMsg msg=new TopicMsg();
                  msg.data=data;
                  msg.topic=topic;
                  queue.offer(msg);
                }
                }
            });
            sub.setDaemon(true);
            sub.setName("subscribetopic");
            sub.start();
        }
    }

    /**
     * 订阅主题
     * @param topics
     */
    public static  void  subscribe(String[] topics) {
        if(topics==null)
        {
            return;
        }
        for (String topic:topics
             ) {
            subscribe(topic);
        }
    }


}
