-- ============================================
-- 学生管理系统 数据库初始化脚本
-- 数据库: MySQL
-- ============================================

CREATE DATABASE IF NOT EXISTS student_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_manager;

-- ============================================
-- 1. 学生表 (student)
-- status: 0=普通学生, 1=班长, 2=休学
-- ============================================
DROP TABLE IF EXISTS `grade`;
DROP TABLE IF EXISTS `teacher_to_class`;
DROP TABLE IF EXISTS `course`;
DROP TABLE IF EXISTS `class`;
DROP TABLE IF EXISTS `student`;
DROP TABLE IF EXISTS `teacher`;

CREATE TABLE `student` (
    `s_id`       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '学生ID(主键)',
    `username`   VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`   VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `name`       VARCHAR(50)  NOT NULL COMMENT '姓名',
    `cl_id`      BIGINT       DEFAULT NULL COMMENT '所属班级ID(外键)',
    `status`     TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0=普通学生, 1=班长, 2=休学',
    `major`      VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `email`      VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    `phone`      VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    PRIMARY KEY (`s_id`),
    UNIQUE KEY `uk_student_username` (`username`),
    KEY `idx_student_cl_id` (`cl_id`),
    KEY `idx_student_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- ============================================
-- 2. 教师表 (teacher)
-- status: 0=普通教师, 1=超级管理员, 2=退休教师
-- ============================================
CREATE TABLE `teacher` (
    `t_id`       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '教师ID(主键)',
    `username`   VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`   VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `name`       VARCHAR(50)  NOT NULL COMMENT '姓名',
    `status`     TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0=普通教师, 1=超级管理员, 2=退休教师',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '所属院系',
    `title`      VARCHAR(50)  DEFAULT NULL COMMENT '职称',
    `email`      VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    `phone`      VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    PRIMARY KEY (`t_id`),
    UNIQUE KEY `uk_teacher_username` (`username`),
    KEY `idx_teacher_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- ============================================
-- 3. 班级表 (class)
-- s_id 外键对应班长(student表中status=1的学生)
-- ============================================
CREATE TABLE `class` (
    `cl_id`     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '班级ID(主键)',
    `classname` VARCHAR(100) NOT NULL COMMENT '班级名称',
    `s_id`      BIGINT      DEFAULT NULL COMMENT '班长ID(外键,对应student.status=1)',
    PRIMARY KEY (`cl_id`),
    UNIQUE KEY `uk_class_classname` (`classname`),
    KEY `idx_class_s_id` (`s_id`),
    CONSTRAINT `fk_class_monitor` FOREIGN KEY (`s_id`) REFERENCES `student`(`s_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 为student表添加cl_id外键
ALTER TABLE `student` ADD CONSTRAINT `fk_student_class` FOREIGN KEY (`cl_id`) REFERENCES `class`(`cl_id`) ON DELETE SET NULL;

-- ============================================
-- 4. 课程表 (course)
-- ============================================
CREATE TABLE `course` (
    `c_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '课程ID(主键)',
    `t_id`    BIGINT       NOT NULL COMMENT '授课教师ID(外键)',
    `subject` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `about`   TEXT         DEFAULT NULL COMMENT '课程介绍',
    PRIMARY KEY (`c_id`),
    KEY `idx_course_t_id` (`t_id`),
    CONSTRAINT `fk_course_teacher` FOREIGN KEY (`t_id`) REFERENCES `teacher`(`t_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- ============================================
-- 5. 成绩表 (grade)
-- 联合主键: s_id + c_id
-- 学生选课后在此生成记录, score默认为0
-- ============================================
CREATE TABLE `grade` (
    `s_id`  BIGINT        NOT NULL COMMENT '学生ID',
    `c_id`  BIGINT        NOT NULL COMMENT '课程ID',
    `score` DECIMAL(5,2)  NOT NULL DEFAULT 0 COMMENT '成绩(默认0)',
    PRIMARY KEY (`s_id`, `c_id`),
    KEY `idx_grade_c_id` (`c_id`),
    CONSTRAINT `fk_grade_student` FOREIGN KEY (`s_id`) REFERENCES `student`(`s_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_grade_course` FOREIGN KEY (`c_id`) REFERENCES `course`(`c_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- ============================================
-- 6. 教师班级对应表 (teacher_to_class)
-- ============================================
CREATE TABLE `teacher_to_class` (
    `ttc_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID(主键)',
    `t_id`   BIGINT NOT NULL COMMENT '教师ID(外键)',
    `c_id`   BIGINT NOT NULL COMMENT '课程ID(外键)',
    PRIMARY KEY (`ttc_id`),
    UNIQUE KEY `uk_ttc_t_c` (`t_id`, `c_id`),
    KEY `idx_ttc_c_id` (`c_id`),
    CONSTRAINT `fk_ttc_teacher` FOREIGN KEY (`t_id`) REFERENCES `teacher`(`t_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ttc_course` FOREIGN KEY (`c_id`) REFERENCES `course`(`c_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师班级对应表';

-- ============================================
-- 7. 操作日志表 (operation_log)
-- ============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `log_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID(主键)',
    `user_type`      VARCHAR(20)  NOT NULL COMMENT '用户类型: STUDENT/TEACHER',
    `user_id`        BIGINT       NOT NULL COMMENT '操作用户ID',
    `operation_type` VARCHAR(50)  NOT NULL COMMENT '操作类型: UPDATE_PROFILE等',
    `target_table`   VARCHAR(50)  NOT NULL COMMENT '目标表名',
    `target_id`      BIGINT       NOT NULL COMMENT '目标记录ID',
    `old_value`      TEXT         DEFAULT NULL COMMENT '修改前的值(JSON)',
    `new_value`      TEXT         DEFAULT NULL COMMENT '修改后的值(JSON)',
    `ip_address`     VARCHAR(50)  DEFAULT NULL COMMENT '操作IP地址',
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`log_id`),
    KEY `idx_log_user` (`user_type`, `user_id`),
    KEY `idx_log_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 初始数据插入
-- ============================================

-- 插入超级管理员 (BCrypt加密密码: "admin123")
INSERT INTO `teacher` (`username`, `password`, `name`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 1);

-- 插入普通教师
INSERT INTO `teacher` (`username`, `password`, `name`, `status`) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '张老师', 0),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '李老师', 0),
('teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '王老师(退休)', 2);

-- 插入学生
INSERT INTO `student` (`username`, `password`, `name`, `status`, `major`) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '张三', 0, '计算机科学与技术'),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '李四', 0, '软件工程'),
('student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '王五', 0, '数据科学'),
('student4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '赵六', 2, '网络工程');

-- 插入班级
INSERT INTO `class` (`classname`) VALUES ('计算机科学1班'), ('软件工程1班'), ('数据科学1班');

-- 设置班长 (student中status=1的学生需要先存在)
INSERT INTO `student` (`username`, `password`, `name`, `status`, `major`, `cl_id`) VALUES
('monitor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '班长A', 1, '计算机科学与技术', 1),
('monitor2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '班长B', 1, '软件工程', 2);

-- 设置学生班级
UPDATE `student` SET `cl_id` = 1 WHERE `username` IN ('student1', 'monitor1');
UPDATE `student` SET `cl_id` = 2 WHERE `username` IN ('student2', 'monitor2');
UPDATE `student` SET `cl_id` = 3 WHERE `username` = 'student3';
UPDATE `student` SET `cl_id` = 1 WHERE `username` = 'student4';

-- 更新班级的班长
UPDATE `class` SET `s_id` = (SELECT `s_id` FROM `student` WHERE `username` = 'monitor1') WHERE `cl_id` = 1;
UPDATE `class` SET `s_id` = (SELECT `s_id` FROM `student` WHERE `username` = 'monitor2') WHERE `cl_id` = 2;

-- 插入课程
INSERT INTO `course` (`t_id`, `subject`, `about`) VALUES
(2, 'Java程序设计', '面向对象的Java编程课程，涵盖基础语法、多线程、网络编程'),
(2, '数据库原理', '关系型数据库理论与MySQL实践'),
(3, 'Web前端开发', 'HTML/CSS/JavaScript/Vue3框架开发'),
(3, '数据结构与算法', '基本数据结构与经典算法分析与实现');

-- 插入选课记录 (成绩默认0)
INSERT INTO `grade` (`s_id`, `c_id`) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 4),
(3, 2), (3, 3), (3, 4),
(5, 1), (5, 2),
(6, 3), (6, 4);

-- 更新部分成绩
UPDATE `grade` SET `score` = 85.5 WHERE `s_id` = 1 AND `c_id` = 1;
UPDATE `grade` SET `score` = 92.0 WHERE `s_id` = 1 AND `c_id` = 2;
UPDATE `grade` SET `score` = 78.0 WHERE `s_id` = 2 AND `c_id` = 1;
UPDATE `grade` SET `score` = 88.0 WHERE `s_id` = 3 AND `c_id` = 2;
