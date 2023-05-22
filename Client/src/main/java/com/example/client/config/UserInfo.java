package com.example.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("UserInfo")
@Data
public class UserInfo {
    @Value("${rim.user.id}")
    private long userID;
}
