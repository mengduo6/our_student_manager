import request from './request'

export function getStudentInfo() {
  return request.get('/student/info')
}

export function getStudentCourses() {
  return request.get('/student/courses')
}

export function enrollCourse(courseId) {
  return request.post(`/student/enroll/${courseId}`)
}

export function dropCourse(courseId) {
  return request.delete(`/student/unenroll/${courseId}`)
}

export function getStudentGrades() {
  return request.get('/student/grades')
}

export function updateStudentProfile(data) {
  return request.put('/student/profile', data)
}
