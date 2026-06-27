package com.example.studentmanager.repository;

import com.example.studentmanager.entity.ClassEntity;
import com.example.studentmanager.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    boolean existsByUsername(String username);
    Student findByClazzAndStatus(ClassEntity clazz, Integer status);
}
