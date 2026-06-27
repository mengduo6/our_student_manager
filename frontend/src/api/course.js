import request from './request'

export function getAllAvailableCourses() {
  return request.get('/courses')
}

export function getCourseDetail(courseId) {
  return request.get(`/courses/${courseId}`)
}
