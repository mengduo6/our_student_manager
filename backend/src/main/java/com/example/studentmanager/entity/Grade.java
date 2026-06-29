package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("grade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @TableId(value = "s_id", type = IdType.INPUT)
    private Long sId;

    @TableId(value = "c_id", type = IdType.INPUT)
    private Long cId;

    @TableField(exist = false)
    private Student student;

    @TableField(exist = false)
    private Course course;

    @TableField("score")
    @Builder.Default
    private Integer score = 0;
}
