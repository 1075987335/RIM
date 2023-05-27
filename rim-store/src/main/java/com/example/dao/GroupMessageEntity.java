package com.example.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("group_message")
public class GroupMessageEntity {

    Integer id;

    Long groupId;

    Long fromUser;

    Long messageId;

    String body;
}
