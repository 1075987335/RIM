package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dao.UserEntity;
import org.springframework.stereotype.Repository;

@Repository("UserMapper")
public interface UserMapper extends BaseMapper<UserEntity> {
}
