package com.example.studentmanager.repository;

import com.example.studentmanager.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Grade.GradeId> {
    @Query("SELECT g FROM Grade g WHERE g.student.sId = :sId")
    List<Grade> findByStudentSId(Long sId);

    @Query("SELECT g FROM Grade g WHERE g.course.cId = :cId")
    List<Grade> findByCourseCId(Long cId);

    @Query("SELECT g FROM Grade g WHERE g.student.sId = :sId AND g.course.cId = :cId")
    Optional<Grade> findByStudentSIdAndCourseCId(Long sId, Long cId);

    @Modifying
    @Query("DELETE FROM Grade g WHERE g.student.sId = :sId AND g.course.cId = :cId")
    void deleteByStudentSIdAndCourseCId(Long sId, Long cId);

    @Query("SELECT COUNT(g) > 0 FROM Grade g WHERE g.student.sId = :sId AND g.course.cId = :cId")
    boolean existsByStudentSIdAndCourseCId(Long sId, Long cId);
}
