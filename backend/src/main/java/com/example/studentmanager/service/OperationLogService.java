package com.example.studentmanager.service;

import com.example.studentmanager.entity.OperationLog;
import com.example.studentmanager.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Transactional
    public void log(String userType, Long userId, String operationType,
                    String targetTable, Long targetId,
                    String oldValue, String newValue, String ipAddress) {
        OperationLog log = OperationLog.builder()
                .userType(userType)
                .userId(userId)
                .operationType(operationType)
                .targetTable(targetTable)
                .targetId(targetId)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(ipAddress)
                .build();
        operationLogMapper.insert(log);
    }
}
