package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    @Select("SELECT * FROM operation_log WHERE user_type = #{userType} AND user_id = #{userId} ORDER BY created_at DESC")
    List<OperationLog> selectByUserTypeAndUserIdOrderByCreatedAtDesc(
            @Param("userType") String userType, @Param("userId") Long userId);
}
