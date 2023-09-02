package com.example.route.thread.ack;

import com.example.common.constant.Constants;
import com.example.common.proto.IM_Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class UnprocessedRequests {
    /**
     * 私聊消息的ACKList
     */
    private final Map<Long, IM_Message> P2P_UNPROCESS_MAP = new ConcurrentHashMap<>();

    /**
     * 群聊消息的ACKList
     */
    private final Map<String, IM_Message> GROUP_UNPROCESS_MAP = new ConcurrentHashMap<>();

    /**
     * 保存TID和IM_Message的映射关系，针对群聊来说，因为群聊一个消息可能会有多个确认
     */
    private final Map<Long, IM_Message> TID_CACHE = new ConcurrentHashMap<>();

    /**
     * 放入元素
     *
     * @param message
     */
    public void put(IM_Message message) {
        byte type = message.getHeader().getType();
        long msgId = message.getHeader().getMID();
        long gid = message.getHeader().getGID();
        long tid = message.getHeader().getTID();
        if (type == Constants.CommandType.P2P_MSG) {
            P2P_UNPROCESS_MAP.put(msgId, message);
            log.info("消息：{}加入等待ACKList成功", message.getHeader().getMID());
        } else if (type == Constants.CommandType.GROUP_MSG) {
            //生成群聊key
            String id = generateId(msgId, gid, tid);
            GROUP_UNPROCESS_MAP.put(id, message);
            log.info("群聊消息：{}:{}:{}加入等待ACKList成功", msgId, tid, gid);
        } else {
            log.info("类型[{}]无需添加计时器", type);
        }
    }

    /**
     * 群聊key生成器
     *
     * @param msgId
     * @param tid
     * @param gid
     * @return
     */
    private String generateId(long msgId, long tid, long gid) {
        return msgId + "" + tid + "" + gid;
    }

    public void finish(long msgId) {
        IM_Message message = P2P_UNPROCESS_MAP.get(msgId);
        if (message != null)
            P2P_UNPROCESS_MAP.remove(msgId);
        log.info("单聊消息确认完成！");
    }

    public void finish(long msgId, long tid, long gid) {
        String id = generateId(msgId, tid, gid);
        IM_Message message = GROUP_UNPROCESS_MAP.get(id);
        if (message != null)
            GROUP_UNPROCESS_MAP.remove(id);
        log.info("群聊消息确认完成");
    }

    public IM_Message get(long messageId) {
        IM_Message message = P2P_UNPROCESS_MAP.get(messageId);
        if (message == null) {
            log.info("消息已经处理过了");
            return null;
        }
        return message;
    }

    public IM_Message get(long mid, long tid, long gid) {
        String id = generateId(mid, tid, gid);
        IM_Message message = GROUP_UNPROCESS_MAP.get(id);
        return message;
    }

    public int P2PMessageSize() {
        return P2P_UNPROCESS_MAP.size();
    }

    public int GroupMessageSize() {
        return GROUP_UNPROCESS_MAP.size();
    }
}
