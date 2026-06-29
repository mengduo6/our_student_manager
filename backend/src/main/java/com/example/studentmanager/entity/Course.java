package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @TableId(value = "c_id", type = IdType.AUTO)
    private Long cId;

    @TableField("t_id")
    private Long tId;

    @TableField(exist = false)
    private Teacher teacher;

    @TableField("subject")
    private String subject;

    @TableField("about")
    private String about;
}
