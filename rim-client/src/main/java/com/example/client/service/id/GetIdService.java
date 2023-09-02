package com.example.client.service.id;

import com.example.GetIDService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class GetIdService {

    @DubboReference
    GetIDService getIDService;

    public long getNextId(String key) {
        return getIDService.getId(key).getId();
    }
}
