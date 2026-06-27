import authStore from '../stores/auth'

const ROLE_STUDENT = 'STUDENT'
const ROLE_TEACHER = 'TEACHER'
const ROLE_SUPER_ADMIN = 'SUPER_ADMIN'
const ROLE_MONITOR = 'MONITOR'

const routes = [
  {
    path: '/',
    redirect: () => {
      const store = authStore
      if (!store.isLoggedIn) return '/login'
      if (store.role === ROLE_SUPER_ADMIN) return '/admin'
      if (store.role === ROLE_TEACHER) return '/teacher'
      if (store.role === ROLE_MONITOR) return '/monitor'
      if (store.role === ROLE_STUDENT) return '/student'
      return '/login'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { guest: true }
  },
  {
    path: '/register-teacher',
    name: 'TeacherRegister',
    component: () => import('../views/TeacherRegisterView.vue'),
    meta: { guest: true }
  },
  {
    path: '/student',
    name: 'StudentDashboard',
    component: () => import('../views/StudentView.vue'),
    meta: { requiresAuth: true, roles: [ROLE_STUDENT] }
  },
  {
    path: '/student/profile',
    name: 'StudentProfileEdit',
    component: () => import('../views/StudentProfileEdit.vue'),
    meta: { requiresAuth: true, roles: [ROLE_STUDENT] }
  },
  {
    path: '/student/courses',
    name: 'CourseList',
    component: () => import('../views/CourseListView.vue'),
    meta: { requiresAuth: true, roles: [ROLE_STUDENT] }
  },
  {
    path: '/student/grades',
    name: 'GradeView',
    component: () => import('../views/GradeView.vue'),
    meta: { requiresAuth: true, roles: [ROLE_STUDENT] }
  },
  {
    path: '/teacher',
    name: 'TeacherDashboard',
    component: () => import('../views/TeacherView.vue'),
    meta: { requiresAuth: true, roles: [ROLE_TEACHER, ROLE_SUPER_ADMIN] }
  },
  {
    path: '/teacher/profile',
    name: 'TeacherProfileEdit',
    component: () => import('../views/TeacherProfileEdit.vue'),
    meta: { requiresAuth: true, roles: [ROLE_TEACHER, ROLE_SUPER_ADMIN] }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminView.vue'),
    meta: { requiresAuth: true, roles: [ROLE_SUPER_ADMIN] }
  },
  {
    path: '/monitor',
    name: 'MonitorDashboard',
    component: () => import('../views/MonitorView.vue'),
    meta: { requiresAuth: true, roles: [ROLE_MONITOR, ROLE_SUPER_ADMIN] }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

export default routes
