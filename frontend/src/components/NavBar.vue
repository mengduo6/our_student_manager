<template>
  <nav class="navbar">
    <div class="navbar-inner container">
      <router-link to="/" class="navbar-brand">
        <span class="brand-icon">📚</span>
        <span class="brand-text">学生管理系统</span>
      </router-link>

      <button class="hamburger" @click="menuOpen = !menuOpen" :aria-label="menuOpen ? '关闭菜单' : '打开菜单'">
        <span></span>
        <span></span>
        <span></span>
      </button>

      <div class="navbar-menu" :class="{ open: menuOpen }">
        <div class="navbar-start">
          <template v-if="auth.isLoggedIn">
            <router-link v-if="auth.role === 'STUDENT'" to="/student" class="nav-link" @click="menuOpen = false">我的课程</router-link>
            <router-link v-if="auth.role === 'STUDENT'" to="/student/profile" class="nav-link" @click="menuOpen = false">编辑资料</router-link>
            <router-link v-if="auth.role === 'STUDENT'" to="/student/courses" class="nav-link" @click="menuOpen = false">选课中心</router-link>
            <router-link v-if="auth.role === 'STUDENT'" to="/student/grades" class="nav-link" @click="menuOpen = false">成绩查询</router-link>
            <router-link v-if="auth.role === 'MONITOR'" to="/monitor" class="nav-link" @click="menuOpen = false">班级管理</router-link>
            <router-link v-if="auth.role === 'TEACHER' || auth.role === 'SUPER_ADMIN'" to="/teacher" class="nav-link" @click="menuOpen = false">教学管理</router-link>
            <router-link v-if="auth.role === 'TEACHER' || auth.role === 'SUPER_ADMIN'" to="/teacher/profile" class="nav-link" @click="menuOpen = false">编辑资料</router-link>
            <router-link v-if="auth.role === 'SUPER_ADMIN'" to="/admin" class="nav-link" @click="menuOpen = false">系统管理</router-link>
          </template>
        </div>

        <div class="navbar-end">
          <template v-if="auth.isLoggedIn">
            <div class="user-info">
              <span class="user-avatar">{{ avatarText }}</span>
              <span class="user-name">{{ auth.user?.username || auth.user?.name || '用户' }}</span>
              <span class="role-badge" :class="roleClass">{{ roleLabel }}</span>
            </div>
            <button class="btn btn-outline btn-sm" @click="handleLogout">退出登录</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-primary btn-sm" @click="menuOpen = false">登录</router-link>
            <router-link to="/register" class="btn btn-outline btn-sm" @click="menuOpen = false">注册</router-link>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import authStore from '../stores/auth'

const auth = authStore
const router = useRouter()
const menuOpen = ref(false)

const avatarText = computed(() => {
  const name = auth.user?.name || auth.user?.username || '用'
  return name.charAt(0).toUpperCase()
})

const roleLabel = computed(() => {
  const map = { STUDENT: '学生', TEACHER: '教师', MONITOR: '班长', SUPER_ADMIN: '超级管理员' }
  return map[auth.role] || ''
})

const roleClass = computed(() => {
  const map = { STUDENT: 'badge-primary', TEACHER: 'badge-warning', MONITOR: 'badge-warning', SUPER_ADMIN: 'badge-danger' }
  return map[auth.role] || 'badge-gray'
})

function handleLogout() {
  auth.logout()
  menuOpen.value = false
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--navbar-height);
  background: var(--white);
  border-bottom: 1px solid var(--gray-200);
  z-index: 100;
  box-shadow: var(--shadow-sm);
}

.navbar-inner {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 1.5rem;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--gray-900);
  font-weight: 700;
  font-size: 1.125rem;
  text-decoration: none;
  flex-shrink: 0;
}

.brand-icon {
  font-size: 1.5rem;
}

.navbar-menu {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: space-between;
  gap: 1rem;
}

.navbar-start {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.navbar-end {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.nav-link {
  padding: 0.5rem 0.875rem;
  border-radius: var(--radius);
  color: var(--gray-600);
  font-size: 0.875rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}

.nav-link:hover {
  background: var(--gray-50);
  color: var(--gray-900);
}

.nav-link.router-link-active {
  background: var(--primary-light);
  color: var(--primary);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-avatar {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: var(--primary);
  color: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.8125rem;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--gray-700);
}

.hamburger {
  display: none;
  flex-direction: column;
  gap: 4px;
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
}

.hamburger span {
  display: block;
  width: 22px;
  height: 2px;
  background: var(--gray-700);
  border-radius: 2px;
  transition: all 0.2s;
}

@media (max-width: 768px) {
  .hamburger {
    display: flex;
    margin-left: auto;
  }

  .hamburger span {
    transition: all 0.3s;
  }

  .navbar-menu {
    display: none;
    position: absolute;
    top: var(--navbar-height);
    left: 0;
    right: 0;
    background: var(--white);
    flex-direction: column;
    padding: 1rem;
    border-bottom: 1px solid var(--gray-200);
    box-shadow: var(--shadow-md);
    align-items: stretch;
  }

  .navbar-menu.open {
    display: flex;
  }

  .navbar-start {
    flex-direction: column;
    align-items: stretch;
  }

  .navbar-end {
    flex-direction: column;
    align-items: stretch;
    padding-top: 0.75rem;
    border-top: 1px solid var(--gray-100);
    margin-top: 0.5rem;
  }

  .nav-link {
    padding: 0.625rem 0.75rem;
  }

  .user-info {
    padding: 0.5rem 0;
  }
}
</style>
