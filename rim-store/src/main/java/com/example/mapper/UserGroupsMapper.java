package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dao.UserGroupsEntity;
import org.springframework.stereotype.Repository;

@Repository("UserGroupsMapper")
public interface UserGroupsMapper extends BaseMapper<UserGroupsEntity> {
}
