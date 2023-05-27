package com.example.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class UserEntity {
    Long id;
}
