package com.example.studentmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studentmanager.dto.ApiResponse;
import com.example.studentmanager.entity.ClassEntity;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.mapper.ClassMapper;
import com.example.studentmanager.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;

    /**
     * 班长查看同班同学列表（含基本信息，不含密码和成绩）
     */
    @GetMapping("/classmates")
    public ApiResponse<List<Map<String, Object>>> getClassmates(
            @RequestParam Long classId) {
        try {
            List<Student> classmates = studentMapper.selectList(
                    new LambdaQueryWrapper<Student>().eq(Student::getClId, classId));

            List<Map<String, Object>> result = new ArrayList<>();
            for (Student s : classmates) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", s.getSId());
                map.put("username", s.getUsername());
                map.put("name", s.getName());
                map.put("major", s.getMajor());
                map.put("status", s.getStatus() == 0 ? "在读" : s.getStatus() == 1 ? "班长" : "休学");
                // Populate class info
                ClassEntity clazz = s.getClId() != null ? classMapper.selectById(s.getClId()) : null;
                map.put("className", clazz != null ? clazz.getClassname() : null);
                result.add(map);
            }
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 班长修改学生基本信息（如姓名、专业）
     * 不可修改: 密码、状态(status)、用户名
     */
    @PutMapping("/students/{studentId}")
    @Transactional
    public ApiResponse<String> updateStudentBasicInfo(
            @PathVariable Long studentId,
            @RequestBody Map<String, String> body) {
        try {
            Student student = studentMapper.selectById(studentId);
            if (student == null) throw new RuntimeException("学生不存在");

            String name = body.get("name");
            String major = body.get("major");

            if (name != null && !name.isBlank()) {
                student.setName(name);
            }
            if (major != null) {
                student.setMajor(major);
            }

            studentMapper.updateById(student);
            return ApiResponse.success("学生信息更新成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
