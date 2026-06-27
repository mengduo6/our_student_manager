import request from './request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function registerStudent(data) {
  return request.post('/auth/register-student', data)
}

export function registerTeacher(data) {
  return request.post('/auth/register-teacher', data)
}

export function getUserInfo() {
  return request.get('/auth/me')
}
