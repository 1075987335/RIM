package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dao.GroupMessageEntity;
import org.springframework.stereotype.Repository;

//数据访问层的一个注解
@Repository("GroupHistoryMapper")
public interface GroupHistoryMapper extends BaseMapper<GroupMessageEntity> {
}
