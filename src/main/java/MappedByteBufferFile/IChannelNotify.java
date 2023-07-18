package MappedByteBufferFile;

/**
 * 监听通道操作
 */
public interface IChannelNotify {
    void  sendMsg(IWorker worker);
}
