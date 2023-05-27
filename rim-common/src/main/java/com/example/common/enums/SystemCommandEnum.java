package com.example.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum SystemCommandEnum {

            P2P(":p2p","单聊发送消息","P2PMsgCommand"),
          GROUP(":group","群聊发送消息，:p2p [用户ID] [消息]","GroupMsgCommand"),
       SHUTDOWN(":q", "关闭客户端，:q" , "ShutdownCommand"),
          LOGIN(":login", "登陆用户，:login [用户ID]","LoginCommand"),
           INFO(":info", "查看用户信息，:info", "UserInfoCommand"),
           TEST(":test", "压测", "P2PSendTestCommand")
    ;

    /** 枚举值码 */
    private final String commandType;

    /** 枚举描述 */
    private final String desc;

    /**
     * 实现类
     */
    private final String clazz ;


    /**
     * 构建一个 。
     * @param commandType 枚举值码。
     * @param desc 枚举描述。
     */
    private SystemCommandEnum(String commandType, String desc, String clazz) {
        this.commandType = commandType;
        this.desc = desc;
        this.clazz = clazz ;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String getCommandType() {
        return commandType;
    }
    /**
     * 获取 class。
     * @return class。
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * 得到枚举描述。
     * @return 枚举描述。
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String code() {
        return commandType;
    }

    /**
     * 得到枚举描述。
     * @return 枚举描述。
     */
    public String message() {
        return desc;
    }


    public static Map<String,String> getAllClazz() {
        Map<String,String> map = new HashMap<String, String>(16) ;
        for (SystemCommandEnum status : values()) {
            map.put(status.getCommandType().trim(),"com.example.client.service.command." + status.getClazz()) ;
        }
        return map;
    }
}