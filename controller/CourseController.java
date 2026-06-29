package com.example.studentmanager.controller;

import com.example.studentmanager.dto.ApiResponse;
import com.example.studentmanager.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAllCourses() {
        try {
            List<Map<String, Object>> courses = courseService.getAllCourses();
            return ApiResponse.success(courses);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
