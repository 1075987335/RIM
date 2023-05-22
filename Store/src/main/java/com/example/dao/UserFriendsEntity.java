package com.example.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_friend_list")
public class UserFriendsEntity {

    Integer id;

    Long userId;

    Long friendId;

    Long latestMessageId;
}
