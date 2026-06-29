package com.example.studentmanager.controller;

import com.example.studentmanager.dto.ApiResponse;
import com.example.studentmanager.entity.*;
import com.example.studentmanager.mapper.*;
import com.example.studentmanager.service.OperationLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final ClassMapper classMapper;
    private final GradeMapper gradeMapper;
    private final PasswordEncoder passwordEncoder;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    // ================================================================
    //  STUDENT MANAGEMENT
    // ================================================================

    @GetMapping("/students")
    public ApiResponse<List<Map<String, Object>>> getAllStudents() {
        try {
            List<Student> students = studentMapper.selectList(null);
            List<Map<String, Object>> result = students.stream().map(s -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", s.getSId());
                map.put("username", s.getUsername());
                map.put("name", s.getName());
                map.put("major", s.getMajor());
                map.put("email", s.getEmail());
                map.put("phone", s.getPhone());
                map.put("status", s.getStatus());
                map.put("statusLabel", s.getStatus() == 0 ? "在读" : s.getStatus() == 1 ? "班长" : "休学");
                ClassEntity clazz = s.getClId() != null ? classMapper.selectById(s.getClId()) : null;
                map.put("className", clazz != null ? clazz.getClassname() : null);
                map.put("classId", clazz != null ? clazz.getClId() : null);
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/students")
    @Transactional
    public ApiResponse<Map<String, Object>> createStudent(@RequestBody Map<String, String> body,
                                                           HttpServletRequest request) {
        try {
            String username = body.get("username");
            String password = body.getOrDefault("password", "123456");
            String name = body.get("name");
            if (username == null || username.isBlank()) return ApiResponse.error(400, "用户名不能为空");
            if (name == null || name.isBlank()) return ApiResponse.error(400, "姓名不能为空");
            if (studentMapper.existsByUsername(username) || teacherMapper.existsByUsername(username))
                return ApiResponse.error(400, "用户名已存在");

            Student student = Student.builder()
                    .username(username.trim())
                    .password(passwordEncoder.encode(password))
                    .name(name.trim())
                    .major(body.get("major"))
                    .email(body.get("email"))
                    .phone(body.get("phone"))
                    .status(0)
                    .build();

            if (body.get("classId") != null) {
                student.setClId(Long.parseLong(body.get("classId")));
            }

            studentMapper.insert(student);

            logOp("STUDENT", student.getSId(), "CREATE", "student", null, toJson(studentToMap(student)), request.getRemoteAddr());

            Map<String, Object> result = studentToMap(student);
            return ApiResponse.success("学生创建成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/students/{studentId}")
    @Transactional
    public ApiResponse<Map<String, Object>> updateStudent(@PathVariable Long studentId,
                                                           @RequestBody Map<String, String> body,
                                                           HttpServletRequest request) {
        try {
            Student student = studentMapper.selectById(studentId);
            if (student == null) throw new RuntimeException("学生不存在");
            String oldJson = toJson(studentToMap(student));

            if (body.containsKey("name")) {
                String n = body.get("name");
                if (n == null || n.trim().isEmpty()) return ApiResponse.error(400, "姓名不能为空");
                student.setName(n.trim());
            }
            if (body.containsKey("major")) student.setMajor(blankToNull(body.get("major")));
            if (body.containsKey("email")) student.setEmail(blankToNull(body.get("email")));
            if (body.containsKey("phone")) student.setPhone(blankToNull(body.get("phone")));
            if (body.containsKey("classId")) {
                String classIdVal = body.get("classId");
                student.setClId(classIdVal != null && !classIdVal.isEmpty() ? Long.parseLong(classIdVal) : null);
            }

            studentMapper.updateById(student);

            logOp("STUDENT", studentId, "UPDATE", "student", oldJson, toJson(studentToMap(student)), request.getRemoteAddr());

            return ApiResponse.success("学生信息更新成功", studentToMap(student));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/students/{studentId}")
    @Transactional
    public ApiResponse<String> deleteStudent(@PathVariable Long studentId, HttpServletRequest request) {
        try {
            Student student = studentMapper.selectById(studentId);
            if (student == null) throw new RuntimeException("学生不存在");
            String oldJson = toJson(studentToMap(student));

            // Remove from grade records first
            gradeMapper.deleteByStudentSId(studentId);

            // Clear monitor reference in class
            if (student.getClId() != null) {
                ClassEntity clazz = classMapper.selectById(student.getClId());
                if (clazz != null && clazz.getMonitorId() != null && clazz.getMonitorId().equals(studentId)) {
                    clazz.setMonitorId(null);
                    classMapper.updateById(clazz);
                }
            }

            studentMapper.deleteById(studentId);

            logOp("STUDENT", studentId, "DELETE", "student", oldJson, null, request.getRemoteAddr());

            return ApiResponse.success("学生已删除");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ================================================================
    //  TEACHER MANAGEMENT
    // ================================================================

    @GetMapping("/teachers")
    public ApiResponse<List<Map<String, Object>>> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherMapper.selectList(null);
            List<Map<String, Object>> result = teachers.stream().map(t -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", t.getTId());
                map.put("username", t.getUsername());
                map.put("name", t.getName());
                map.put("department", t.getDepartment());
                map.put("title", t.getTitle());
                map.put("email", t.getEmail());
                map.put("phone", t.getPhone());
                map.put("status", t.getStatus());
                map.put("statusLabel", t.getStatus() == 0 ? "在职" : t.getStatus() == 1 ? "超级管理员" : "退休");
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/teachers")
    @Transactional
    public ApiResponse<Map<String, Object>> createTeacher(@RequestBody Map<String, String> body,
                                                           HttpServletRequest request) {
        try {
            String username = body.get("username");
            String password = body.getOrDefault("password", "123456");
            String name = body.get("name");
            if (username == null || username.isBlank()) return ApiResponse.error(400, "用户名不能为空");
            if (name == null || name.isBlank()) return ApiResponse.error(400, "姓名不能为空");
            if (teacherMapper.existsByUsername(username) || studentMapper.existsByUsername(username))
                return ApiResponse.error(400, "用户名已存在");

            Teacher teacher = Teacher.builder()
                    .username(username.trim())
                    .password(passwordEncoder.encode(password))
                    .name(name.trim())
                    .department(body.get("department"))
                    .title(body.get("title"))
                    .email(body.get("email"))
                    .phone(body.get("phone"))
                    .status(0)
                    .build();

            teacherMapper.insert(teacher);

            logOp("TEACHER", teacher.getTId(), "CREATE", "teacher", null, toJson(teacherToMap(teacher)), request.getRemoteAddr());

            return ApiResponse.success("教师创建成功", teacherToMap(teacher));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/teachers/{teacherId}")
    @Transactional
    public ApiResponse<Map<String, Object>> updateTeacher(@PathVariable Long teacherId,
                                                           @RequestBody Map<String, String> body,
                                                           HttpServletRequest request) {
        try {
            Teacher teacher = teacherMapper.selectById(teacherId);
            if (teacher == null) throw new RuntimeException("教师不存在");
            String oldJson = toJson(teacherToMap(teacher));

            if (body.containsKey("name")) {
                String n = body.get("name");
                if (n == null || n.trim().isEmpty()) return ApiResponse.error(400, "姓名不能为空");
                teacher.setName(n.trim());
            }
            if (body.containsKey("department")) teacher.setDepartment(blankToNull(body.get("department")));
            if (body.containsKey("title")) teacher.setTitle(blankToNull(body.get("title")));
            if (body.containsKey("email")) teacher.setEmail(blankToNull(body.get("email")));
            if (body.containsKey("phone")) teacher.setPhone(blankToNull(body.get("phone")));

            teacherMapper.updateById(teacher);

            logOp("TEACHER", teacherId, "UPDATE", "teacher", oldJson, toJson(teacherToMap(teacher)), request.getRemoteAddr());

            return ApiResponse.success("教师信息更新成功", teacherToMap(teacher));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/teachers/{teacherId}")
    @Transactional
    public ApiResponse<String> deleteTeacher(@PathVariable Long teacherId, HttpServletRequest request) {
        try {
            Teacher teacher = teacherMapper.selectById(teacherId);
            if (teacher == null) throw new RuntimeException("教师不存在");

            if (teacher.getStatus() == 1) {
                long adminCount = teacherMapper.selectList(null).stream().filter(t -> t.getStatus() == 1).count();
                if (adminCount <= 1) return ApiResponse.error(400, "不能删除唯一的超级管理员");
            }

            String oldJson = toJson(teacherToMap(teacher));

            // Remove associated courses and their grade records
            List<Course> courses = courseMapper.selectByTeacherTId(teacherId);
            for (Course c : courses) {
                gradeMapper.deleteByCourseCId(c.getCId());
            }
            // Delete all courses by this teacher
            courses.forEach(c -> courseMapper.deleteById(c.getCId()));

            teacherMapper.deleteById(teacherId);

            logOp("TEACHER", teacherId, "DELETE", "teacher", oldJson, null, request.getRemoteAddr());

            return ApiResponse.success("教师已删除");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ================================================================
    //  STUDENT STATUS / MONITOR MANAGEMENT
    // ================================================================

    @PutMapping("/students/{studentId}/status")
    @Transactional
    public ApiResponse<String> updateStudentStatus(@PathVariable Long studentId,
                                                    @RequestBody Map<String, Integer> body,
                                                    HttpServletRequest request) {
        try {
            Student student = studentMapper.selectById(studentId);
            if (student == null) throw new RuntimeException("学生不存在");
            Integer oldStatus = student.getStatus();
            Integer newStatus = body.get("status");
            if (newStatus == null || (newStatus != 0 && newStatus != 1 && newStatus != 2))
                return ApiResponse.error(400, "状态值无效 (0=在读,1=班长,2=休学)");
            student.setStatus(newStatus);
            studentMapper.updateById(student);

            logOp("STUDENT", studentId, "UPDATE_STATUS", "student",
                    "{\"status\":" + oldStatus + "}", "{\"status\":" + newStatus + "}", request.getRemoteAddr());

            return ApiResponse.success("状态更新成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/students/{studentId}/appoint-monitor")
    @Transactional
    public ApiResponse<String> appointMonitor(@PathVariable Long studentId, HttpServletRequest request) {
        try {
            Student student = studentMapper.selectById(studentId);
            if (student == null) throw new RuntimeException("学生不存在");
            if (student.getClId() != null) {
                Student oldMonitor = studentMapper.selectByClIdAndStatus(student.getClId(), 1);
                if (oldMonitor != null && !oldMonitor.getSId().equals(studentId)) {
                    oldMonitor.setStatus(0);
                    studentMapper.updateById(oldMonitor);
                }
            }
            student.setStatus(1);
            studentMapper.updateById(student);
            if (student.getClId() != null) {
                ClassEntity clazz = classMapper.selectById(student.getClId());
                if (clazz != null) {
                    clazz.setMonitorId(studentId);
                    classMapper.updateById(clazz);
                }
            }

            logOp("STUDENT", studentId, "APPOINT_MONITOR", "student", null, "{\"monitor\":true}", request.getRemoteAddr());

            return ApiResponse.success("班长任命成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/students/{studentId}/dismiss-monitor")
    @Transactional
    public ApiResponse<String> dismissMonitor(@PathVariable Long studentId, HttpServletRequest request) {
        try {
            Student student = studentMapper.selectById(studentId);
            if (student == null) throw new RuntimeException("学生不存在");
            if (student.getStatus() != 1) return ApiResponse.error(400, "该学生不是班长");
            student.setStatus(0);
            studentMapper.updateById(student);
            if (student.getClId() != null) {
                ClassEntity clazz = classMapper.selectById(student.getClId());
                if (clazz != null) {
                    clazz.setMonitorId(null);
                    classMapper.updateById(clazz);
                }
            }

            logOp("STUDENT", studentId, "DISMISS_MONITOR", "student", null, "{\"monitor\":false}", request.getRemoteAddr());

            return ApiResponse.success("班长已解除");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/teachers/{teacherId}/status")
    @Transactional
    public ApiResponse<String> updateTeacherStatus(@PathVariable Long teacherId,
                                                    @RequestBody Map<String, Integer> body,
                                                    HttpServletRequest request) {
        try {
            Teacher teacher = teacherMapper.selectById(teacherId);
            if (teacher == null) throw new RuntimeException("教师不存在");
            Integer oldStatus = teacher.getStatus();
            Integer newStatus = body.get("status");
            if (newStatus == null || (newStatus != 0 && newStatus != 1 && newStatus != 2))
                return ApiResponse.error(400, "状态值无效");

            if (oldStatus == 1 && newStatus != 1) {
                long adminCount = teacherMapper.selectList(null).stream().filter(t -> t.getStatus() == 1).count();
                if (adminCount <= 1) return ApiResponse.error(400, "不能修改最后一个超级管理员的状态");
            }

            teacher.setStatus(newStatus);
            teacherMapper.updateById(teacher);

            logOp("TEACHER", teacherId, "UPDATE_STATUS", "teacher",
                    "{\"status\":" + oldStatus + "}", "{\"status\":" + newStatus + "}", request.getRemoteAddr());

            return ApiResponse.success("状态更新成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ================================================================
    //  COURSE MANAGEMENT
    // ================================================================

    @GetMapping("/courses")
    public ApiResponse<List<Map<String, Object>>> getAllCourses() {
        try {
            List<Course> courses = courseMapper.selectList(null);
            List<Map<String, Object>> result = courses.stream().map(c -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getCId());
                map.put("name", c.getSubject());
                map.put("about", c.getAbout());
                Teacher teacher = c.getTId() != null ? teacherMapper.selectById(c.getTId()) : null;
                map.put("teacherName", teacher != null ? teacher.getName() : null);
                map.put("teacherId", teacher != null ? teacher.getTId() : null);
                map.put("enrollmentCount", gradeMapper.selectByCourseCId(c.getCId()).size());
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/courses/{courseId}")
    @Transactional
    public ApiResponse<String> deleteCourse(@PathVariable Long courseId, HttpServletRequest request) {
        try {
            Course course = courseMapper.selectById(courseId);
            if (course == null) throw new RuntimeException("课程不存在");
            gradeMapper.deleteByCourseCId(courseId);
            courseMapper.deleteById(courseId);

            logOp("COURSE", courseId, "DELETE", "course", toJson(Map.of("name", course.getSubject())), null, request.getRemoteAddr());

            return ApiResponse.success("课程已删除");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ================================================================
    //  CLASS MANAGEMENT
    // ================================================================

    @GetMapping("/classes")
    public ApiResponse<List<Map<String, Object>>> getAllClasses() {
        try {
            List<ClassEntity> classes = classMapper.selectList(null);
            List<Map<String, Object>> result = classes.stream().map(c -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getClId());
                map.put("name", c.getClassname());
                Student monitor = c.getMonitorId() != null ? studentMapper.selectById(c.getMonitorId()) : null;
                map.put("monitorName", monitor != null ? monitor.getName() : null);
                long studentCount = studentMapper.selectList(null).stream()
                        .filter(s -> s.getClId() != null && s.getClId().equals(c.getClId())).count();
                map.put("studentCount", studentCount);
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/classes")
    @Transactional
    public ApiResponse<Map<String, Object>> createClass(@RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            String classname = body.get("classname");
            if (classname == null || classname.isBlank()) return ApiResponse.error(400, "班级名称不能为空");
            if (classMapper.existsByClassname(classname)) return ApiResponse.error(400, "班级名称已存在");
            ClassEntity clazz = ClassEntity.builder().classname(classname).build();
            classMapper.insert(clazz);

            logOp("CLASS", clazz.getClId(), "CREATE", "class", null, toJson(Map.of("classname", classname)), request.getRemoteAddr());

            Map<String, Object> result = Map.of("id", clazz.getClId(), "name", clazz.getClassname());
            return ApiResponse.success("创建成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/classes/{classId}")
    @Transactional
    public ApiResponse<Map<String, Object>> updateClass(@PathVariable Long classId,
                                                         @RequestBody Map<String, String> body,
                                                         HttpServletRequest request) {
        try {
            ClassEntity clazz = classMapper.selectById(classId);
            if (clazz == null) throw new RuntimeException("班级不存在");
            String oldName = clazz.getClassname();
            if (body.containsKey("classname") || body.containsKey("name")) {
                String newName = body.containsKey("classname") ? body.get("classname") : body.get("name");
                if (newName == null || newName.isBlank()) return ApiResponse.error(400, "班级名称不能为空");
                clazz.setClassname(newName.trim());
            }
            classMapper.updateById(clazz);

            logOp("CLASS", classId, "UPDATE", "class",
                    "{\"classname\":\"" + oldName + "\"}", "{\"classname\":\"" + clazz.getClassname() + "\"}", request.getRemoteAddr());

            Map<String, Object> result = Map.of("id", clazz.getClId(), "name", clazz.getClassname());
            return ApiResponse.success("更新成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ================================================================
    //  BATCH OPERATIONS
    // ================================================================

    @PostMapping("/students/batch-delete")
    @Transactional
    public ApiResponse<String> batchDeleteStudents(@RequestBody Map<String, List<Long>> body, HttpServletRequest request) {
        try {
            List<Long> ids = body.get("ids");
            if (ids == null || ids.isEmpty()) return ApiResponse.error(400, "请选择要删除的学生");
            int count = 0;
            for (Long id : ids) {
                if (studentMapper.selectById(id) != null) {
                    gradeMapper.deleteByStudentSId(id);
                    studentMapper.deleteById(id);
                    logOp("STUDENT", id, "BATCH_DELETE", "student", null, null, request.getRemoteAddr());
                    count++;
                }
            }
            return ApiResponse.success("已批量删除 " + count + " 名学生");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/teachers/batch-delete")
    @Transactional
    public ApiResponse<String> batchDeleteTeachers(@RequestBody Map<String, List<Long>> body, HttpServletRequest request) {
        try {
            List<Long> ids = body.get("ids");
            if (ids == null || ids.isEmpty()) return ApiResponse.error(400, "请选择要删除的教师");
            int count = 0;
            for (Long id : ids) {
                Teacher t = teacherMapper.selectById(id);
                if (t != null && t.getStatus() != 1) {
                    List<Course> courses = courseMapper.selectByTeacherTId(id);
                    for (Course c : courses) {
                        gradeMapper.deleteByCourseCId(c.getCId());
                        courseMapper.deleteById(c.getCId());
                    }
                    teacherMapper.deleteById(id);
                    logOp("TEACHER", id, "BATCH_DELETE", "teacher", null, null, request.getRemoteAddr());
                    count++;
                }
            }
            return ApiResponse.success("已批量删除 " + count + " 名教师");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ================================================================
    //  HELPERS
    // ================================================================

    private Map<String, Object> studentToMap(Student s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getSId());
        m.put("username", s.getUsername());
        m.put("name", s.getName());
        m.put("major", s.getMajor());
        m.put("email", s.getEmail());
        m.put("phone", s.getPhone());
        m.put("status", s.getStatus());
        m.put("statusLabel", s.getStatus() == 0 ? "在读" : s.getStatus() == 1 ? "班长" : "休学");
        ClassEntity clazz = s.getClId() != null ? classMapper.selectById(s.getClId()) : null;
        m.put("className", clazz != null ? clazz.getClassname() : null);
        m.put("classId", clazz != null ? clazz.getClId() : null);
        return m;
    }

    private Map<String, Object> teacherToMap(Teacher t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", t.getTId());
        m.put("username", t.getUsername());
        m.put("name", t.getName());
        m.put("department", t.getDepartment());
        m.put("title", t.getTitle());
        m.put("email", t.getEmail());
        m.put("phone", t.getPhone());
        m.put("status", t.getStatus());
        m.put("statusLabel", t.getStatus() == 0 ? "在职" : t.getStatus() == 1 ? "超级管理员" : "退休");
        return m;
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); } catch (JsonProcessingException e) { return "{}"; }
    }

    private String blankToNull(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }

    private void logOp(String userType, Long userId, String opType, String targetTable, String oldVal, String newVal, String ip) {
        try {
            operationLogService.log(userType, userId, opType, targetTable, userId, oldVal, newVal, ip);
        } catch (Exception ignored) {}
    }
}
