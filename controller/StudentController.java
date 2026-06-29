package com.example.studentmanager.controller;

import com.example.studentmanager.dto.ApiResponse;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getStudentInfo(Authentication authentication) {
        try {
            Student student = studentService.getStudentByUsername(authentication.getName());
            Map<String, Object> info = new java.util.LinkedHashMap<>();
            info.put("id", student.getSId());
            info.put("name", student.getName());
            info.put("username", student.getUsername());
            info.put("major", student.getMajor());
            info.put("email", student.getEmail());
            info.put("phone", student.getPhone());
            info.put("status", student.getStatus());
            info.put("statusText", student.getStatus() == 0 ? "在读" : student.getStatus() == 1 ? "班长" : "休学");
            info.put("className", student.getClazz() != null ? student.getClazz().getClassname() : null);
            info.put("classId", student.getClazz() != null ? student.getClazz().getClId() : null);
            return ApiResponse.success(info);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ApiResponse<Map<String, Object>> updateProfile(@RequestBody Map<String, String> body,
                                                           Authentication authentication,
                                                           HttpServletRequest request) {
        try {
            Student student = studentService.getStudentByUsername(authentication.getName());
            String ip = request.getRemoteAddr();
            Map<String, Object> result = studentService.updateProfile(student.getSId(), body, ip);
            return ApiResponse.success("个人信息更新成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/courses")
    public ApiResponse<List<Map<String, Object>>> getMyCourses(Authentication authentication) {
        try {
            Student student = studentService.getStudentByUsername(authentication.getName());
            List<Map<String, Object>> courses = studentService.getEnrolledCourses(student.getSId());
            return ApiResponse.success(courses);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/enroll/{courseId}")
    public ApiResponse<String> enrollCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            Student student = studentService.getStudentByUsername(authentication.getName());
            studentService.enrollCourse(student.getSId(), courseId);
            return ApiResponse.success("选课成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/unenroll/{courseId}")
    public ApiResponse<String> unenrollCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            Student student = studentService.getStudentByUsername(authentication.getName());
            studentService.unenrollCourse(student.getSId(), courseId);
            return ApiResponse.success("退课成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/grades")
    public ApiResponse<List<Map<String, Object>>> getMyGrades(Authentication authentication) {
        try {
            Student student = studentService.getStudentByUsername(authentication.getName());
            List<Map<String, Object>> grades = studentService.getMyGrades(student.getSId());
            return ApiResponse.success(grades);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
