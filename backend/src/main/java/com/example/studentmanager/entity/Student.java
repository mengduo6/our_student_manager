package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @TableId(value = "s_id", type = IdType.AUTO)
    private Long sId;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("name")
    private String name;

    @TableField("cl_id")
    private Long clId;

    @TableField(exist = false)
    private ClassEntity clazz;

    @TableField("status")
    @Builder.Default
    private Integer status = 0;

    @TableField("major")
    private String major;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;
}
