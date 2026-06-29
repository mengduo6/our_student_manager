package com.example.studentmanager.mapper;

import com.example.studentmanager.entity.Grade;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GradeMapper {

    @Select("SELECT * FROM grade WHERE s_id = #{sId}")
    @Results({
        @Result(column = "s_id", property = "sId"),
        @Result(column = "c_id", property = "cId")
    })
    List<Grade> selectByStudentSId(@Param("sId") Long sId);

    @Select("SELECT * FROM grade WHERE c_id = #{cId}")
    @Results({
        @Result(column = "s_id", property = "sId"),
        @Result(column = "c_id", property = "cId")
    })
    List<Grade> selectByCourseCId(@Param("cId") Long cId);

    @Select("SELECT * FROM grade WHERE s_id = #{sId} AND c_id = #{cId}")
    @Results({
        @Result(column = "s_id", property = "sId"),
        @Result(column = "c_id", property = "cId")
    })
    Grade selectBySIdAndCId(@Param("sId") Long sId, @Param("cId") Long cId);

    @Insert("INSERT INTO grade (s_id, c_id, score) VALUES (#{sId}, #{cId}, #{score})")
    int insert(Grade grade);

    @Update("UPDATE grade SET score = #{score} WHERE s_id = #{sId} AND c_id = #{cId}")
    int updateById(Grade grade);

    @Delete("DELETE FROM grade WHERE s_id = #{sId} AND c_id = #{cId}")
    int deleteBySIdAndCId(@Param("sId") Long sId, @Param("cId") Long cId);

    @Select("SELECT COUNT(*) > 0 FROM grade WHERE s_id = #{sId} AND c_id = #{cId}")
    boolean existsBySIdAndCId(@Param("sId") Long sId, @Param("cId") Long cId);

    @Delete("DELETE FROM grade WHERE s_id = #{sId}")
    int deleteByStudentSId(@Param("sId") Long sId);

    @Delete("DELETE FROM grade WHERE c_id = #{cId}")
    int deleteByCourseCId(@Param("cId") Long cId);
}
