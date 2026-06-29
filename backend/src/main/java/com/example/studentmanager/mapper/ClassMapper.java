package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.ClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ClassMapper extends BaseMapper<ClassEntity> {

    @Select("SELECT * FROM class WHERE classname = #{classname}")
    ClassEntity selectByClassname(@Param("classname") String classname);

    @Select("SELECT COUNT(*) > 0 FROM class WHERE classname = #{classname}")
    boolean existsByClassname(@Param("classname") String classname);
}
