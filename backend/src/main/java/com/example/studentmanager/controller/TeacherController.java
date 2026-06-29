package com.example.studentmanager.controller;

import com.example.studentmanager.dto.ApiResponse;
import com.example.studentmanager.entity.Course;
import com.example.studentmanager.entity.Teacher;
import com.example.studentmanager.mapper.GradeMapper;
import com.example.studentmanager.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final GradeMapper gradeMapper;

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getTeacherInfo(Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", teacher.getTId());
            info.put("name", teacher.getName());
            info.put("username", teacher.getUsername());
            info.put("department", teacher.getDepartment());
            info.put("title", teacher.getTitle());
            info.put("email", teacher.getEmail());
            info.put("phone", teacher.getPhone());
            info.put("status", teacher.getStatus());
            info.put("statusText", teacher.getStatus() == 0 ? "在职" : teacher.getStatus() == 1 ? "管理员" : "退休");
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
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            String ip = request.getRemoteAddr();
            Map<String, Object> result = teacherService.updateProfile(teacher.getTId(), body, ip);
            return ApiResponse.success("个人信息更新成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/courses")
    public ApiResponse<List<Map<String, Object>>> getMyCourses(Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            List<Course> courses = teacherService.getMyCourses(teacher.getTId());
            List<Map<String, Object>> result = new ArrayList<>();
            for (Course course : courses) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", course.getCId());
                map.put("name", course.getSubject());
                map.put("about", course.getAbout());
                map.put("teacherId", course.getTeacher() != null ? course.getTeacher().getTId() : null);
                map.put("teacherName", course.getTeacher() != null ? course.getTeacher().getName() : null);
                map.put("enrollmentCount", gradeMapper.selectByCourseCId(course.getCId()).size());
                result.add(map);
            }
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/courses")
    public ApiResponse<Map<String, Object>> createCourse(@RequestBody Map<String, String> body,
                                                          Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            String subject = body.get("subject");
            String about = body.get("about");
            if (subject == null || subject.isBlank()) {
                return ApiResponse.error(400, "课程名称不能为空");
            }
            Course course = teacherService.createCourse(teacher.getTId(), subject, about);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", course.getCId());
            map.put("name", course.getSubject());
            map.put("about", course.getAbout());
            return ApiResponse.success("创建成功", map);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/courses/{courseId}")
    public ApiResponse<Map<String, Object>> updateCourse(@PathVariable Long courseId,
                                                          @RequestBody Map<String, String> body,
                                                          Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            String subject = body.get("subject");
            String about = body.get("about");
            Course course = teacherService.updateCourse(teacher.getTId(), courseId, subject, about);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", course.getCId());
            map.put("name", course.getSubject());
            map.put("about", course.getAbout());
            return ApiResponse.success("更新成功", map);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/courses/{courseId}")
    public ApiResponse<String> deleteCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            teacherService.deleteCourse(courseId);
            return ApiResponse.success("删除成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/courses/{courseId}/students")
    public ApiResponse<List<Map<String, Object>>> getCourseStudents(@PathVariable Long courseId,
                                                                     Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            List<Map<String, Object>> students = teacherService.getCourseStudents(teacher.getTId(), courseId);
            return ApiResponse.success(students);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/courses/{courseId}/students/{studentId}/grade")
    public ApiResponse<String> updateGrade(@PathVariable Long courseId, @PathVariable Long studentId,
                                            @RequestBody Map<String, Integer> body,
                                            Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            Integer score = body.get("score");
            if (score == null) {
                return ApiResponse.error(400, "成绩不能为空");
            }
            teacherService.updateGrade(teacher.getTId(), studentId, courseId, score);
            return ApiResponse.success("成绩更新成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/courses/{courseId}/students/{studentId}")
    public ApiResponse<String> removeStudentFromCourse(@PathVariable Long courseId, @PathVariable Long studentId,
                                                        Authentication authentication) {
        try {
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            teacherService.removeStudentFromCourse(teacher.getTId(), studentId, courseId);
            return ApiResponse.success("退课成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
