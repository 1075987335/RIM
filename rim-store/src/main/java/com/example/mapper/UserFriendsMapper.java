package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dao.UserFriendsEntity;
import org.springframework.stereotype.Repository;

@Repository("UserFriendsMapper")
public interface UserFriendsMapper extends BaseMapper<UserFriendsEntity> {
}
