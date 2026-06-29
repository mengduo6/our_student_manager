package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("teacher_to_class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherToClass {

    @TableId(value = "ttc_id", type = IdType.AUTO)
    private Long ttcId;

    @TableField("t_id")
    private Long tId;

    @TableField("c_id")
    private Long cId;

    @TableField(exist = false)
    private Teacher teacher;

    @TableField(exist = false)
    private Course course;
}
