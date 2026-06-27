package com.example.studentmanager.repository;

import com.example.studentmanager.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findByUserTypeAndUserIdOrderByCreatedAtDesc(String userType, Long userId);
}
