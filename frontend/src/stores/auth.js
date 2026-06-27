import { reactive } from 'vue'
import { login as apiLogin, registerStudent as apiRegisterStudent, registerTeacher as apiRegisterTeacher, getUserInfo } from '../api/auth'
import { setAuthStore } from '../api/request'

const state = reactive({
  user: null,
  token: localStorage.getItem('token') || null,
  isLoggedIn: !!localStorage.getItem('token'),
  role: localStorage.getItem('role') || null
})

// Wire auth store into request module
setAuthStore(state)

async function login(username, password, role) {
  const res = await apiLogin({ username, password, role })
  const data = res.data || res
  state.token = data.token
  state.role = data.role || role
  state.isLoggedIn = true
  localStorage.setItem('token', data.token)
  localStorage.setItem('role', data.role || role)
  await fetchUserInfo()
}

async function register(username, password, name) {
  await apiRegisterStudent({ username, password, name })
}

async function registerTeacher(username, password, name) {
  await apiRegisterTeacher({ username, password, name })
}

async function fetchUserInfo() {
  if (!state.token) {
    state.isLoggedIn = false
    state.user = null
    return
  }
  try {
    const res = await getUserInfo()
    state.user = res.data || res
    state.role = (res.data || res).role || state.role
    state.isLoggedIn = true
  } catch (err) {
    console.error('Failed to fetch user info:', err)
    state.isLoggedIn = false
    state.user = null
    state.token = null
    state.role = null
    localStorage.removeItem('token')
    localStorage.removeItem('role')
  }
}

function logout() {
  state.token = null
  state.role = null
  state.user = null
  state.isLoggedIn = false
  localStorage.removeItem('token')
  localStorage.removeItem('role')
}

export default {
  state,
  get user() { return state.user },
  get token() { return state.token },
  get isLoggedIn() { return state.isLoggedIn },
  get role() { return state.role },
  login,
  register,
  registerTeacher,
  logout,
  fetchUserInfo
}
