package com.example.studentmanager.service;

import com.example.studentmanager.dto.LoginRequest;
import com.example.studentmanager.dto.LoginResponse;
import com.example.studentmanager.dto.RegisterRequest;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.entity.Teacher;
import com.example.studentmanager.repository.StudentRepository;
import com.example.studentmanager.repository.TeacherRepository;
import com.example.studentmanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        // Try student first
        var studentOpt = studentRepository.findByUsername(request.getUsername());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            String role = student.getStatus() == 1 ? "MONITOR" : "STUDENT";
            String token = jwtUtil.generateToken(student.getUsername(), role);
            return LoginResponse.builder()
                    .token(token)
                    .role(role)
                    .username(student.getUsername())
                    .name(student.getName())
                    .build();
        }

        // Try teacher
        var teacherOpt = teacherRepository.findByUsername(request.getUsername());
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), teacher.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            String role = teacher.getStatus() == 1 ? "SUPER_ADMIN" : "TEACHER";
            String token = jwtUtil.generateToken(teacher.getUsername(), role);
            return LoginResponse.builder()
                    .token(token)
                    .role(role)
                    .username(teacher.getUsername())
                    .name(teacher.getName())
                    .build();
        }

        throw new RuntimeException("用户不存在");
    }

    public void registerStudent(RegisterRequest request) {
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (teacherRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Student student = Student.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .major(request.getMajor())
                .status(0)
                .build();
        studentRepository.save(student);
    }

    public void registerTeacher(RegisterRequest request) {
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (teacherRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Teacher teacher = Teacher.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .status(0)
                .build();
        teacherRepository.save(teacher);
    }

    public Object getCurrentUser(String username) {
        var studentOpt = studentRepository.findByUsername(username);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String role = student.getStatus() == 1 ? "MONITOR" : "STUDENT";
            Long classId = student.getClazz() != null ? student.getClazz().getClId() : null;
            String className = student.getClazz() != null ? student.getClazz().getClassname() : null;
            return new UserInfo(student.getSId(), student.getUsername(), student.getName(), role, student.getStatus(), student.getMajor(), classId, className);
        }

        var teacherOpt = teacherRepository.findByUsername(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            String role = teacher.getStatus() == 1 ? "SUPER_ADMIN" : "TEACHER";
            return new UserInfo(teacher.getTId(), teacher.getUsername(), teacher.getName(), role, teacher.getStatus(), null, null, null);
        }

        throw new RuntimeException("用户不存在");
    }

    public record UserInfo(Long id, String username, String name, String role, Integer status, String major, Long classId, String className) {}
}
