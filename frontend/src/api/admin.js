import request from './request'

// ========== Students ==========

export function getAllStudents() {
  return request.get('/admin/students')
}

export function createStudent(data) {
  return request.post('/admin/students', data)
}

export function updateStudent(studentId, data) {
  return request.put(`/admin/students/${studentId}`, data)
}

export function deleteStudent(studentId) {
  return request.delete(`/admin/students/${studentId}`)
}

export function updateStudentStatus(studentId, status) {
  return request.put(`/admin/students/${studentId}/status`, { status })
}

export function appointMonitor(studentId) {
  return request.put(`/admin/students/${studentId}/appoint-monitor`)
}

export function dismissMonitor(studentId) {
  return request.put(`/admin/students/${studentId}/dismiss-monitor`)
}

export function batchDeleteStudents(ids) {
  return request.post('/admin/students/batch-delete', { ids })
}

// ========== Teachers ==========

export function getAllTeachers() {
  return request.get('/admin/teachers')
}

export function createTeacher(data) {
  return request.post('/admin/teachers', data)
}

export function updateTeacher(teacherId, data) {
  return request.put(`/admin/teachers/${teacherId}`, data)
}

export function deleteTeacher(teacherId) {
  return request.delete(`/admin/teachers/${teacherId}`)
}

export function updateTeacherStatus(teacherId, status) {
  return request.put(`/admin/teachers/${teacherId}/status`, { status })
}

export function batchDeleteTeachers(ids) {
  return request.post('/admin/teachers/batch-delete', { ids })
}

// ========== Classes ==========

export function getAllClasses() {
  return request.get('/admin/classes')
}

export function createClass(data) {
  return request.post('/admin/classes', data)
}

export function updateClass(classId, data) {
  return request.put(`/admin/classes/${classId}`, data)
}

// ========== Courses ==========

export function getAllCourses() {
  return request.get('/admin/courses')
}

export function deleteAdminCourse(courseId) {
  return request.delete(`/admin/courses/${courseId}`)
}
