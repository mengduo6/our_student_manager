package com.example.studentmanager.service;

import com.example.studentmanager.dto.LoginRequest;
import com.example.studentmanager.dto.LoginResponse;
import com.example.studentmanager.dto.RegisterRequest;
import com.example.studentmanager.entity.ClassEntity;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.entity.Teacher;
import com.example.studentmanager.mapper.ClassMapper;
import com.example.studentmanager.mapper.StudentMapper;
import com.example.studentmanager.mapper.TeacherMapper;
import com.example.studentmanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final ClassMapper classMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        // Try student first
        Student student = studentMapper.selectByUsername(request.getUsername());
        if (student != null) {
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
        Teacher teacher = teacherMapper.selectByUsername(request.getUsername());
        if (teacher != null) {
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
        if (studentMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (teacherMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Student student = Student.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .major(request.getMajor())
                .status(0)
                .build();
        studentMapper.insert(student);
    }

    public void registerTeacher(RegisterRequest request) {
        if (studentMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (teacherMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Teacher teacher = Teacher.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .status(0)
                .build();
        teacherMapper.insert(teacher);
    }

    public Object getCurrentUser(String username) {
        Student student = studentMapper.selectByUsername(username);
        if (student != null) {
            String role = student.getStatus() == 1 ? "MONITOR" : "STUDENT";
            Long classId = null;
            String className = null;
            if (student.getClId() != null) {
                ClassEntity clazz = classMapper.selectById(student.getClId());
                if (clazz != null) {
                    classId = clazz.getClId();
                    className = clazz.getClassname();
                }
            }
            return new UserInfo(student.getSId(), student.getUsername(), student.getName(), role, student.getStatus(), student.getMajor(), classId, className);
        }

        Teacher teacher = teacherMapper.selectByUsername(username);
        if (teacher != null) {
            String role = teacher.getStatus() == 1 ? "SUPER_ADMIN" : "TEACHER";
            return new UserInfo(teacher.getTId(), teacher.getUsername(), teacher.getName(), role, teacher.getStatus(), null, null, null);
        }

        throw new RuntimeException("用户不存在");
    }

    public record UserInfo(Long id, String username, String name, String role, Integer status, String major, Long classId, String className) {}
}
