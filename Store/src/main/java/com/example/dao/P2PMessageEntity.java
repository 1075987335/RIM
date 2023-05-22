package com.example.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("p2p_message")
public class P2PMessageEntity {

    @TableId(type = IdType.AUTO)
    Integer id;

    Long toUser;

    Long fromUser;

    Long messageId;

    Integer state;

    String body;
}
