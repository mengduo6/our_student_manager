package com.example.studentmanager.config;

import com.example.studentmanager.entity.Teacher;
import com.example.studentmanager.mapper.TeacherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!teacherMapper.existsByUsername("root")) {
            Teacher root = Teacher.builder()
                    .username("root")
                    .password(passwordEncoder.encode("root"))
                    .name("超级管理员")
                    .status(1)
                    .build();
            teacherMapper.insert(root);
            System.out.println("[DataInitializer] root超级管理员已创建");
        }

        if (!teacherMapper.existsByUsername("admin")) {
            Teacher admin = Teacher.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .name("系统管理员")
                    .status(1)
                    .build();
            teacherMapper.insert(admin);
            System.out.println("[DataInitializer] admin系统管理员已创建");
        }
    }
}
