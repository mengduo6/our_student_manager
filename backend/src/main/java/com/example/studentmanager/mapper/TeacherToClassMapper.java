package com.example.studentmanager.mapper;

import com.example.studentmanager.entity.TeacherToClass;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherToClassMapper {

    @Select("SELECT * FROM teacher_to_class WHERE t_id = #{tId}")
    List<TeacherToClass> selectByTeacherTId(@Param("tId") Long tId);

    @Select("SELECT * FROM teacher_to_class WHERE c_id = #{cId}")
    List<TeacherToClass> selectByCourseCId(@Param("cId") Long cId);

    @Select("SELECT * FROM teacher_to_class WHERE t_id = #{tId} AND c_id = #{cId}")
    TeacherToClass selectByTIdAndCId(@Param("tId") Long tId, @Param("cId") Long cId);

    @Insert("INSERT INTO teacher_to_class (t_id, c_id) VALUES (#{tId}, #{cId})")
    @Options(useGeneratedKeys = true, keyProperty = "ttcId")
    int insert(TeacherToClass entity);

    @Delete("DELETE FROM teacher_to_class WHERE t_id = #{tId} AND c_id = #{cId}")
    int deleteByTIdAndCId(@Param("tId") Long tId, @Param("cId") Long cId);

    @Select("SELECT COUNT(*) > 0 FROM teacher_to_class WHERE t_id = #{tId} AND c_id = #{cId}")
    boolean existsByTIdAndCId(@Param("tId") Long tId, @Param("cId") Long cId);

    @Delete("DELETE FROM teacher_to_class WHERE ttc_id = #{ttcId}")
    int deleteById(@Param("ttcId") Long ttcId);

    @Select("SELECT * FROM teacher_to_class WHERE ttc_id = #{ttcId}")
    TeacherToClass selectById(@Param("ttcId") Long ttcId);
}
