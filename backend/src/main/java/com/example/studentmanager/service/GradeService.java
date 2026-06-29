package com.example.studentmanager.service;

import com.example.studentmanager.entity.*;
import com.example.studentmanager.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeMapper gradeMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

    public List<Grade> getGradesByStudentId(Long studentId) {
        List<Grade> grades = gradeMapper.selectByStudentSId(studentId);
        enrichGrades(grades);
        return grades;
    }

    public List<Grade> getGradesByCourseId(Long courseId) {
        List<Grade> grades = gradeMapper.selectByCourseCId(courseId);
        enrichGrades(grades);
        return grades;
    }

    /**
     * Populate student and course (with teacher) for a list of grades.
     */
    private void enrichGrades(List<Grade> grades) {
        for (Grade grade : grades) {
            if (grade.getSId() != null) {
                grade.setStudent(studentMapper.selectById(grade.getSId()));
            }
            if (grade.getCId() != null) {
                Course course = courseMapper.selectById(grade.getCId());
                if (course != null && course.getTId() != null) {
                    course.setTeacher(teacherMapper.selectById(course.getTId()));
                }
                grade.setCourse(course);
            }
        }
    }
}
