package com.example.studentmanager.service;

import com.example.studentmanager.entity.*;
import com.example.studentmanager.mapper.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final GradeMapper gradeMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final ClassMapper classMapper;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    public List<Map<String, Object>> getEnrolledCourses(Long studentId) {
        List<Grade> grades = gradeMapper.selectByStudentSId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Grade grade : grades) {
            Course course = courseMapper.selectById(grade.getCId());
            Teacher teacher = null;
            if (course != null && course.getTId() != null) {
                teacher = teacherMapper.selectById(course.getTId());
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("courseId", grade.getCId());
            map.put("subject", course != null ? course.getSubject() : null);
            map.put("about", course != null ? course.getAbout() : null);
            map.put("teacherName", teacher != null ? teacher.getName() : null);
            map.put("teacherId", teacher != null ? teacher.getTId() : null);
            map.put("score", grade.getScore());
            result.add(map);
        }

        return result;
    }

    @Transactional
    public void enrollCourse(Long studentId, Long courseId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) throw new RuntimeException("学生不存在");

        if (student.getStatus() == 2) {
            throw new RuntimeException("休学学生不能选课");
        }

        if (gradeMapper.existsBySIdAndCId(studentId, courseId)) {
            throw new RuntimeException("已选过该课程");
        }

        Course course = courseMapper.selectById(courseId);
        if (course == null) throw new RuntimeException("课程不存在");

        Grade grade = Grade.builder()
                .sId(studentId)
                .cId(courseId)
                .score(0)
                .build();
        gradeMapper.insert(grade);
    }

    @Transactional
    public void unenrollCourse(Long studentId, Long courseId) {
        if (!gradeMapper.existsBySIdAndCId(studentId, courseId)) {
            throw new RuntimeException("未选该课程");
        }
        gradeMapper.deleteBySIdAndCId(studentId, courseId);
    }

    public List<Map<String, Object>> getMyGrades(Long studentId) {
        return getEnrolledCourses(studentId);
    }

    public Student getStudentByUsername(String username) {
        Student student = studentMapper.selectByUsername(username);
        if (student == null) throw new RuntimeException("学生不存在");
        // Populate clazz
        if (student.getClId() != null) {
            student.setClazz(classMapper.selectById(student.getClId()));
        }
        return student;
    }

    @Transactional
    public Map<String, Object> updateProfile(Long studentId, Map<String, String> updates, String ipAddress) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) throw new RuntimeException("学生不存在");

        // Capture old values for audit log
        Map<String, String> oldValues = new LinkedHashMap<>();
        oldValues.put("name", student.getName());
        oldValues.put("major", student.getMajor());
        oldValues.put("email", student.getEmail());
        oldValues.put("phone", student.getPhone());

        Map<String, String> newValues = new LinkedHashMap<>();

        if (updates.containsKey("name")) {
            String name = updates.get("name");
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("姓名不能为空");
            }
            if (name.length() > 50) {
                throw new RuntimeException("姓名长度不能超过50个字符");
            }
            student.setName(name.trim());
            newValues.put("name", name.trim());
        }

        if (updates.containsKey("major")) {
            String major = updates.get("major");
            if (major != null && major.length() > 100) {
                throw new RuntimeException("专业名称长度不能超过100个字符");
            }
            student.setMajor(major != null && !major.trim().isEmpty() ? major.trim() : null);
            newValues.put("major", student.getMajor());
        }

        if (updates.containsKey("email")) {
            String email = updates.get("email");
            if (email != null && !email.trim().isEmpty()) {
                if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
                    throw new RuntimeException("邮箱格式不正确");
                }
                if (email.length() > 100) {
                    throw new RuntimeException("邮箱长度不能超过100个字符");
                }
            }
            student.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            newValues.put("email", student.getEmail());
        }

        if (updates.containsKey("phone")) {
            String phone = updates.get("phone");
            if (phone != null && !phone.trim().isEmpty()) {
                if (!phone.matches("^[\\d\\-+() ]{7,20}$")) {
                    throw new RuntimeException("电话号码格式不正确");
                }
            }
            student.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
            newValues.put("phone", student.getPhone());
        }

        studentMapper.updateById(student);

        try {
            operationLogService.log(
                    "STUDENT", studentId, "UPDATE_PROFILE",
                    "student", studentId,
                    objectMapper.writeValueAsString(oldValues),
                    objectMapper.writeValueAsString(newValues),
                    ipAddress
            );
        } catch (JsonProcessingException ignored) {
        }

        // Populate clazz
        ClassEntity clazz = null;
        if (student.getClId() != null) {
            clazz = classMapper.selectById(student.getClId());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", student.getSId());
        result.put("name", student.getName());
        result.put("username", student.getUsername());
        result.put("major", student.getMajor());
        result.put("email", student.getEmail());
        result.put("phone", student.getPhone());
        result.put("status", student.getStatus());
        result.put("className", clazz != null ? clazz.getClassname() : null);
        result.put("classId", clazz != null ? clazz.getClId() : null);
        return result;
    }
}
