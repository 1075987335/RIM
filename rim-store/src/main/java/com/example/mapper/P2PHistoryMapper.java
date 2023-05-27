package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dao.P2PMessageEntity;
import org.springframework.stereotype.Repository;

@Repository("P2PHistoryMapper")
public interface P2PHistoryMapper extends BaseMapper<P2PMessageEntity> {
}
