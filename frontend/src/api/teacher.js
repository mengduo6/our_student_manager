import request from './request'

export function getTeacherInfo() {
  return request.get('/teacher/info')
}

export function getTeacherCourses() {
  return request.get('/teacher/courses')
}

export function createCourse(data) {
  return request.post('/teacher/courses', data)
}

export function updateCourse(courseId, data) {
  return request.put(`/teacher/courses/${courseId}`, data)
}

export function deleteCourse(courseId) {
  return request.delete(`/teacher/courses/${courseId}`)
}

export function getCourseStudents(courseId) {
  return request.get(`/teacher/courses/${courseId}/students`)
}

export function updateStudentGrade(courseId, studentId, data) {
  return request.put(`/teacher/courses/${courseId}/students/${studentId}/grade`, data)
}

export function removeStudentFromCourse(courseId, studentId) {
  return request.delete(`/teacher/courses/${courseId}/students/${studentId}`)
}

export function updateTeacherProfile(data) {
  return request.put('/teacher/profile', data)
}
