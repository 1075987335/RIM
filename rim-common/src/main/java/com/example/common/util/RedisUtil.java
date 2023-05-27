package com.example.common.util;

import com.example.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisUtil {

    private RedisTemplate redisTemplate;

    public RedisUtil(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    /**
     * 使用zset存储，方便批量发送离线消息
     * @param fromId
     * @param toId
     * @param msgId
     * @param msg
     */
    public void setP2PMessage(long fromId,long toId,long msgId,String msg){
        String key = fromId + Constants.RedisConstants.UserP2PMessageStore + toId;

        long id = msgId >> 22;
        //redis中也要保存消息id信息
        String value = msgId + ":" + msg;
        redisTemplate.opsForZSet().add(key,value,id);
    }

    public void deleteP2PMessage(long fromId, long toId, long msgId){
        String key = fromId + Constants.RedisConstants.UserP2PMessageStore + toId;

        long id = msgId >> 22;
        redisTemplate.opsForZSet().removeRangeByScore(key, id, id);
    }

    /**
     * 获取一个msgId之后的所有消息
     * @param fromId
     * @param toId
     * @param msgId
     * @return
     */
    public Set<String> getP2PMessage(long fromId, long toId, Long msgId){
        String key = fromId + Constants.RedisConstants.UserP2PMessageStore + toId;
        long id = msgId >> 22;
        Set<String> set = redisTemplate.opsForZSet().rangeByScore(key, id+1, Long.MAX_VALUE);
        return set;
    }

    /**
     * 使用zset存储，方便批量发送离线消息
     * @param fromId
     * @param groupId
     * @param msgId
     * @param msg
     */
    public void setGroupMessage(long fromId, long groupId, long msgId, String msg){
        String key = Constants.RedisConstants.GorupMessageStore + groupId;

        long id = msgId >> 22;
        //保存消息id信息
        String value = msgId + ":" + msg + ":" + fromId;
        redisTemplate.opsForZSet().add(key, value, id);
    }

    /**
     * 获取群聊离线消息
     * @param groupId
     * @param msgId
     * @return
     */
    public Set<String> getGroupMessage(long groupId, long msgId){
        String key = Constants.RedisConstants.GorupMessageStore + groupId;

        long id = msgId >> 22;
        Set<String> set = redisTemplate.opsForZSet().rangeByScore(key, id+1, Long.MAX_VALUE);
        return set;
    }

    /**
     * 使用hash存储
     * @param UID
     * @param state
     */
    public void setUserLoginState(long UID,int state){
        String key = Constants.RedisConstants.LoginState;
        redisTemplate.opsForHash().put(key,UID,state);
    }

    /**
     * 查看用户是否在线
     * @param UID
     * @return
     */
    public boolean getUserLoginState(long UID){
        String key = Constants.RedisConstants.LoginState;
        Object o = redisTemplate.opsForHash().get(key,UID);
        if(o == null)
            return false;
        int state = (int)o;
        if(state == 0)
            return false;
        return true;
    }

    /**
     * 使用set来存储群成员id
     * @param groupId
     * @param UID
     */
    public void setGroupMember(long groupId,long UID){
        String key = groupId + Constants.RedisConstants.GroupMember;
        redisTemplate.opsForSet().add(key,UID);
    }

    /**
     * 获取群成员列表
     * @param groupId
     * @return
     */
    public List<Long> getAllGroupMember(long groupId){
        String key = groupId + Constants.RedisConstants.GroupMember;
        Set members = redisTemplate.opsForSet().members(key);
        List<Long> list = new ArrayList<>();
        for(Object str: members){
            list.add(Long.parseLong(String.valueOf(str)));
        }
        return list;
    }

    /**
     * 使用set来存储p2p去重消息
     * @param msgId
     */
    public void cacheP2PMessage(long msgId, long expireTime){
        String key = Constants.RedisConstants.P2PCacheMessage + msgId;
        redisTemplate.opsForValue().set(key, "");
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 使用set来存储group去重信息
     * @param msgId
     */
    public void cacheGroupMessage(long msgId, long groupId, long expireTime, long receiveId){
        String value = groupId + Constants.RedisConstants.GourpCacheMessage + msgId;
        redisTemplate.opsForValue().set(value, "");
        redisTemplate.expire(value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 查看p2p消息是否重复
     * @param msgId
     * @return
     */
    public boolean isP2PMessageExist(long msgId){
        String key = Constants.RedisConstants.P2PCacheMessage + msgId;
        return redisTemplate.hasKey(key);
    }

    /**
     * 查看group消息是否重复
     * @param msgId
     * @return
     */
    public boolean isGroupmessageExist(long msgId, long groupId){
        String key = groupId + Constants.RedisConstants.GourpCacheMessage + msgId;
        return redisTemplate.hasKey(key);
    }

    /**
     * 保存用户路由信息
     * @param server
     * @param UID
     */
    public void setUserRoute(String server, long UID) {
        String key = UID + Constants.RedisConstants.UserRoute;
        redisTemplate.opsForValue().set(key,server);
    }

    /**
     * 获取用户路由信息
     * @param UID
     */
    public String getUserRoute(long UID) {
        String key = UID +Constants.RedisConstants.UserRoute;
        return (String)redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除用户的路由信息，以及修改用户的登陆状态
     * @param UID
     * @return
     */
    public boolean offlineRedisDelete(long UID) {
        String routeKey = UID +Constants.RedisConstants.UserRoute;
        String userRoute = getUserRoute(UID);
        boolean userLoginState = getUserLoginState(UID);
        boolean delete = false;
        //删除用户路由
        if(userRoute != null){
            delete = redisTemplate.delete(routeKey);
        }
        //修改用户登陆状态
        if(delete == true && userLoginState == true){
            setUserLoginState(UID,0);
            return true;
        }
        return false;
    }
}
