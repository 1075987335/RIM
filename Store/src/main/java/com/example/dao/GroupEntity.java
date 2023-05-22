package com.example.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("group")
public class GroupEntity {

    Long id;

    String name;
}
