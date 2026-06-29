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
public class TeacherService {

    private final CourseMapper courseMapper;
    private final GradeMapper gradeMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final TeacherToClassMapper teacherToClassMapper;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    public List<Course> getMyCourses(Long teacherId) {
        List<Course> courses = courseMapper.selectByTeacherTId(teacherId);
        // Populate teacher
        Teacher teacher = teacherMapper.selectById(teacherId);
        for (Course c : courses) {
            c.setTeacher(teacher);
        }
        return courses;
    }

    @Transactional
    public Course createCourse(Long teacherId, String subject, String about) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) throw new RuntimeException("教师不存在");

        if (teacher.getStatus() == 2) {
            throw new RuntimeException("退休教师不能创建课程");
        }

        Course course = Course.builder()
                .tId(teacherId)
                .subject(subject)
                .about(about)
                .build();
        courseMapper.insert(course);
        course.setTeacher(teacher);
        return course;
    }

    @Transactional
    public Course updateCourse(Long teacherId, Long courseId, String subject, String about) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) throw new RuntimeException("课程不存在");

        if (!course.getTId().equals(teacherId)) {
            Teacher teacher = teacherMapper.selectById(teacherId);
            if (teacher == null) throw new RuntimeException("教师不存在");
            if (teacher.getStatus() != 1) {
                throw new RuntimeException("无权限修改该课程");
            }
        }

        if (subject != null) course.setSubject(subject);
        if (about != null) course.setAbout(about);
        courseMapper.updateById(course);

        if (course.getTId() != null) {
            course.setTeacher(teacherMapper.selectById(course.getTId()));
        }
        return course;
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        if (courseMapper.selectById(courseId) == null) {
            throw new RuntimeException("课程不存在");
        }

        List<Grade> grades = gradeMapper.selectByCourseCId(courseId);
        for (Grade g : grades) {
            gradeMapper.deleteBySIdAndCId(g.getSId(), courseId);
        }
        courseMapper.deleteById(courseId);
    }

    @Transactional
    public void updateGrade(Long teacherId, Long studentId, Long courseId, Integer score) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) throw new RuntimeException("课程不存在");

        if (!course.getTId().equals(teacherId)) {
            Teacher teacher = teacherMapper.selectById(teacherId);
            if (teacher == null) throw new RuntimeException("教师不存在");
            if (teacher.getStatus() != 1) {
                throw new RuntimeException("无权限修改该课程的成绩");
            }
        }

        Grade grade = gradeMapper.selectBySIdAndCId(studentId, courseId);
        if (grade == null) throw new RuntimeException("该学生未选修此课程");

        grade.setScore(score);
        gradeMapper.updateById(grade);
    }

    @Transactional
    public void removeStudentFromCourse(Long teacherId, Long studentId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) throw new RuntimeException("课程不存在");

        if (!course.getTId().equals(teacherId)) {
            Teacher teacher = teacherMapper.selectById(teacherId);
            if (teacher == null) throw new RuntimeException("教师不存在");
            if (teacher.getStatus() != 1) {
                throw new RuntimeException("无权限操作该课程");
            }
        }

        if (!gradeMapper.existsBySIdAndCId(studentId, courseId)) {
            throw new RuntimeException("该学生未选修此课程");
        }

        gradeMapper.deleteBySIdAndCId(studentId, courseId);
    }

    public Teacher getTeacherByUsername(String username) {
        Teacher teacher = teacherMapper.selectByUsername(username);
        if (teacher == null) throw new RuntimeException("教师不存在");
        return teacher;
    }

    @Transactional
    public Map<String, Object> updateProfile(Long teacherId, Map<String, String> updates, String ipAddress) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) throw new RuntimeException("教师不存在");

        Map<String, String> oldValues = new LinkedHashMap<>();
        oldValues.put("name", teacher.getName());
        oldValues.put("department", teacher.getDepartment());
        oldValues.put("title", teacher.getTitle());
        oldValues.put("email", teacher.getEmail());
        oldValues.put("phone", teacher.getPhone());

        Map<String, String> newValues = new LinkedHashMap<>();

        if (updates.containsKey("name")) {
            String name = updates.get("name");
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("姓名不能为空");
            }
            if (name.length() > 50) {
                throw new RuntimeException("姓名长度不能超过50个字符");
            }
            teacher.setName(name.trim());
            newValues.put("name", name.trim());
        }

        if (updates.containsKey("department")) {
            String department = updates.get("department");
            if (department != null && department.length() > 100) {
                throw new RuntimeException("院系名称长度不能超过100个字符");
            }
            teacher.setDepartment(department != null && !department.trim().isEmpty() ? department.trim() : null);
            newValues.put("department", teacher.getDepartment());
        }

        if (updates.containsKey("title")) {
            String tit = updates.get("title");
            if (tit != null && tit.length() > 50) {
                throw new RuntimeException("职称长度不能超过50个字符");
            }
            teacher.setTitle(tit != null && !tit.trim().isEmpty() ? tit.trim() : null);
            newValues.put("title", teacher.getTitle());
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
            teacher.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            newValues.put("email", teacher.getEmail());
        }

        if (updates.containsKey("phone")) {
            String phone = updates.get("phone");
            if (phone != null && !phone.trim().isEmpty()) {
                if (!phone.matches("^[\\d\\-+() ]{7,20}$")) {
                    throw new RuntimeException("电话号码格式不正确");
                }
            }
            teacher.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
            newValues.put("phone", teacher.getPhone());
        }

        teacherMapper.updateById(teacher);

        try {
            operationLogService.log(
                    "TEACHER", teacherId, "UPDATE_PROFILE",
                    "teacher", teacherId,
                    objectMapper.writeValueAsString(oldValues),
                    objectMapper.writeValueAsString(newValues),
                    ipAddress
            );
        } catch (JsonProcessingException ignored) {
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", teacher.getTId());
        result.put("name", teacher.getName());
        result.put("username", teacher.getUsername());
        result.put("department", teacher.getDepartment());
        result.put("title", teacher.getTitle());
        result.put("email", teacher.getEmail());
        result.put("phone", teacher.getPhone());
        result.put("status", teacher.getStatus());
        return result;
    }

    public List<Map<String, Object>> getCourseStudents(Long teacherId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) throw new RuntimeException("课程不存在");

        if (!course.getTId().equals(teacherId)) {
            Teacher teacher = teacherMapper.selectById(teacherId);
            if (teacher == null) throw new RuntimeException("教师不存在");
            if (teacher.getStatus() != 1) {
                throw new RuntimeException("无权限查看该课程学生");
            }
        }

        List<Grade> grades = gradeMapper.selectByCourseCId(courseId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Grade grade : grades) {
            Student student = studentMapper.selectById(grade.getSId());
            if (student == null) continue;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", student.getSId());
            map.put("name", student.getName());
            map.put("username", student.getUsername());
            map.put("major", student.getMajor());
            map.put("score", grade.getScore());
            result.add(map);
        }
        return result;
    }
}
