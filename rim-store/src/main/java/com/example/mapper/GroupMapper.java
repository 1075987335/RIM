package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dao.GroupEntity;
import org.springframework.stereotype.Repository;

@Repository("GroupMapper")
public interface GroupMapper extends BaseMapper<GroupEntity> {
}
