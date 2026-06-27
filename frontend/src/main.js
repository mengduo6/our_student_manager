import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'
import routes from './router/index'
import authStore from './stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const { isLoggedIn, role } = authStore

  if (to.meta.guest && isLoggedIn) {
    if (role === 'SUPER_ADMIN') return next('/admin')
    if (role === 'TEACHER') return next('/teacher')
    if (role === 'MONITOR') return next('/monitor')
    if (role === 'STUDENT') return next('/student')
    return next('/login')
  }

  if (to.meta.requiresAuth) {
    if (!isLoggedIn) return next('/login')
    const allowedRoles = to.meta.roles
    if (allowedRoles && !allowedRoles.includes(role)) {
      if (role === 'SUPER_ADMIN') return next('/admin')
      if (role === 'TEACHER') return next('/teacher')
      if (role === 'MONITOR') return next('/monitor')
      if (role === 'STUDENT') return next('/student')
      return next('/login')
    }
  }

  next()
})

const app = createApp(App)

app.use(router)

// Initialize auth before mounting
authStore.fetchUserInfo().finally(() => {
  app.mount('#app')
})
