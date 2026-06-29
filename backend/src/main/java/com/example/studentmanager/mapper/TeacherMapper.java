package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    @Select("SELECT * FROM teacher WHERE username = #{username}")
    Teacher selectByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) > 0 FROM teacher WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);
}
