package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @TableId(value = "t_id", type = IdType.AUTO)
    private Long tId;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("name")
    private String name;

    @TableField("status")
    @Builder.Default
    private Integer status = 0;

    @TableField("department")
    private String department;

    @TableField("title")
    private String title;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;
}
