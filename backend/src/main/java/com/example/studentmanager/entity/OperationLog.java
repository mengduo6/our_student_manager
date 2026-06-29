package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@TableName("operation_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationLog {

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @TableField("user_type")
    private String userType;

    @TableField("user_id")
    private Long userId;

    @TableField("operation_type")
    private String operationType;

    @TableField("target_table")
    private String targetTable;

    @TableField("target_id")
    private Long targetId;

    @TableField("old_value")
    private String oldValue;

    @TableField("new_value")
    private String newValue;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
