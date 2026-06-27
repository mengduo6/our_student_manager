package com.example.studentmanager.repository;

import com.example.studentmanager.entity.TeacherToClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherToClassRepository extends JpaRepository<TeacherToClass, Long> {
    @Query("SELECT ttc FROM TeacherToClass ttc WHERE ttc.teacher.tId = :tId")
    List<TeacherToClass> findByTeacherTId(Long tId);

    @Query("SELECT ttc FROM TeacherToClass ttc WHERE ttc.course.cId = :cId")
    List<TeacherToClass> findByCourseCId(Long cId);

    @Query("SELECT ttc FROM TeacherToClass ttc WHERE ttc.teacher.tId = :tId AND ttc.course.cId = :cId")
    Optional<TeacherToClass> findByTeacherTIdAndCourseCId(Long tId, Long cId);

    @Modifying
    @Query("DELETE FROM TeacherToClass ttc WHERE ttc.teacher.tId = :tId AND ttc.course.cId = :cId")
    void deleteByTeacherTIdAndCourseCId(Long tId, Long cId);

    @Query("SELECT COUNT(ttc) > 0 FROM TeacherToClass ttc WHERE ttc.teacher.tId = :tId AND ttc.course.cId = :cId")
    boolean existsByTeacherTIdAndCourseCId(Long tId, Long cId);
}
