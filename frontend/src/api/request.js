import axios from 'axios'
import { reactive } from 'vue'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Store reference — will be set after auth store is created
let _authStore = null

export function setAuthStore(store) {
  _authStore = store
}

// Request interceptor — attach token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor — handle 401
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      if (_authStore) {
        _authStore.isLoggedIn = false
        _authStore.user = null
        _authStore.token = null
        _authStore.role = null
      }
      window.location.href = '/login'
    }
    const message = error.response?.data?.message || error.message || '请求失败'
    error.displayMessage = message
    return Promise.reject(error)
  }
)

export default request
