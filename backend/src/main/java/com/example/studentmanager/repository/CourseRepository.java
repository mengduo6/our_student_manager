package com.example.studentmanager.repository;

import com.example.studentmanager.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.teacher.tId = :tId")
    List<Course> findByTeacherTId(Long tId);
}
