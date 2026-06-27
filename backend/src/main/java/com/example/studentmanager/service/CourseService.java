package com.example.studentmanager.service;

import com.example.studentmanager.entity.*;
import com.example.studentmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public List<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(course -> {
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
