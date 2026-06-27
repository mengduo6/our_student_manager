package com.example.studentmanager.repository;

import com.example.studentmanager.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    Optional<ClassEntity> findByClassname(String classname);
    boolean existsByClassname(String classname);
}
