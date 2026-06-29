package com.example.studentmanager.service;

import com.example.studentmanager.entity.*;
import com.example.studentmanager.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;

    public List<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseMapper.selectList(null);
        return courses.stream().map(course -> {
            // Populate teacher
            if (course.getTId() != null) {
                course.setTeacher(teacherMapper.selectById(course.getTId()));
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("courseId", course.getCId());
            map.put("subject", course.getSubject());
            map.put("about", course.getAbout());
            map.put("teacherName", course.getTeacher() != null ? course.getTeacher().getName() : null);
            map.put("teacherId", course.getTeacher() != null ? course.getTeacher().getTId() : null);
            return map;
        }).collect(Collectors.toList());
    }
}
