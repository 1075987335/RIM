package com.example.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_group_list")
public class UserGroupsEntity {

    Integer id;

    Long userId;

    Long groupId;

    Long latestMessageId;
}
