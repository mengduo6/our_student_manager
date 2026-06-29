package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @TableId(value = "cl_id", type = IdType.AUTO)
    private Long clId;

    @TableField("classname")
    private String classname;

    @TableField("s_id")
    private Long monitorId;

    @TableField(exist = false)
    private Student monitor;
}
