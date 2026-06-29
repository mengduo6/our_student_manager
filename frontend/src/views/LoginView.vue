﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">登录</h1>
      <p class="auth-subtitle">欢迎回到学生管理系统</p>

      <div v-if="errorMsg" class="message message-error">{{ errorMsg }}</div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label" for="username">用户名</label>
          <input
            id="username"
            v-model="form.username"
            class="form-input"
            type="text"
            placeholder="请输入用户名"
            required
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label class="form-label" for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            class="form-input"
            type="password"
            placeholder="请输入密码"
            required
            autocomplete="current-password"
          />
        </div>

        <div class="form-group">
          <label class="form-label">角色</label>
          <div class="role-selector">
            <label class="role-option" :class="{ active: form.role === 'STUDENT' }">
              <input type="radio" v-model="form.role" value="STUDENT" />
              <span class="role-label">🎓 学生</span>
            </label>
            <label class="role-option" :class="{ active: form.role === 'TEACHER' }">
              <input type="radio" v-model="form.role" value="TEACHER" />
              <span class="role-label">👨‍🏫 教师</span>
            </label>
          </div>
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg" :disabled="loading">
          <span v-if="loading" class="spinner" style="width:1rem;height:1rem;border-width:2px;"></span>
          <span v-else>登录</span>
        </button>
      </form>

      <p class="auth-footer">
        还没有账号？
        <router-link to="/register">学生注册</router-link>
        &nbsp;|&nbsp;
        <router-link to="/register-teacher">教师注册</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import authStore from '../stores/auth'

const router = useRouter()
const form = reactive({
  username: '',
  password: '',
  role: 'STUDENT'
})
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    await authStore.login(form.username, form.password, form.role)
    const role = authStore.role
    if (role === 'SUPER_ADMIN') {
          router.push('/admin')
        } else if (role === 'TEACHER') {
          router.push('/teacher')
        } else if (role === 'MONITOR') {
          router.push('/monitor')
        } else {
          router.push('/student')
        }
  } catch (e) {
    errorMsg.value = e.displayMessage || e.displayMessage || e.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.role-selector {
  display: flex;
  gap: 0.75rem;
}

.role-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.375rem;
  padding: 0.625rem;
  border: 2px solid var(--gray-200);
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.2s;
}

.role-option input {
  display: none;
}

.role-option.active {
  border-color: var(--primary);
  background: var(--primary-light);
}

.role-label {
  font-size: 0.875rem;
  font-weight: 500;
}

.auth-footer {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.875rem;
  color: var(--gray-500);
}
</style>
