import request from './request'

export function getClassmates(classId) {
  return request.get('/monitor/classmates', { params: { classId } })
}

export function updateStudentInfo(studentId, data) {
  return request.put(`/monitor/students/${studentId}`, data)
}
