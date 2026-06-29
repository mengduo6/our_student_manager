package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    @Select("SELECT * FROM student WHERE username = #{username}")
    Student selectByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) > 0 FROM student WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    @Select("SELECT * FROM student WHERE cl_id = #{clId} AND status = #{status}")
    Student selectByClIdAndStatus(@Param("clId") Long clId, @Param("status") Integer status);
}
