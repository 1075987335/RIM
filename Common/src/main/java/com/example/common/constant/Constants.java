package com.example.common.constant;

public class Constants {

    public static class ChannelConstants {

        /**
         * channel 绑定的 userId Key
         */
        public static final String UId = "UId";

        /**
         * channel 绑定的读写时间
         */
        public static final String ReadTime = "readTime";
    }

    public static class RedisConstants {

        /**
         * 用户会话信息
         * 格式：UserId:userSession
         */
        public static final String UserSession = ":userSession";

        /**
         * 单聊离线消息
         * 格式：FromId:P2PMessage:ToId
         */
        public static final String UserP2PMessageStore = ":P2PMessage:";

        /**
         * 群聊离线消息
         * 格式：GroupMessage:GroupId
         */
        public static final String GorupMessageStore = "GroupMessage:";

        /**
         * 用户登陆状态
         * 格式：UserId:LoginState
         */
        public static final String LoginState = "LoginState";

        /**
         * 群成员列表
         * 格式：GroupId:GroupMember
         */
        public static final String GroupMember = ":GroupMember";

        /**
         * 单聊消息去重
         * 格式：P2PCacheMessage:msgId
         */
        public static final String P2PCacheMessage = "P2PCacheMessage:";

        /**
         * 群聊消息去重
         * 格式：groupId:GroupCacheMessage:msgId
         */
        public static final String GourpCacheMessage = ":GroupCacheMessage:";

        /**
         * 用户路由信息
         */
        public static final String UserRoute = ":userRoute";

    }

    public static class RabbitmqConstants {

        public static final String GroupMessage = "storeGroupMessage";

        public static final String GroupACK = "storeGroupACK";

        public static final String P2PMessage = "storeP2PMessage";

        public static final String P2PACK = "storeP2PACK";

        public static final String P2POfflineMessage = "storeOfflineMessage";

        public static final String Routing = "messageRouting";

    }

    public static class CommandType{
        public static final byte LOGIN = 1;

        public static final byte P2P_MSG = 2;

        public static final byte PING = 3;

        public static final byte PONG = 4;

        public static final byte P2P_ACK = 5;

        public static final byte GROUP_MSG = 6;

        public static final byte GROUP_ACK = 7;
    }

    public static class Code{
        public static int MESSAGE_SYMBLE = Integer.MIN_VALUE;
    }

}
