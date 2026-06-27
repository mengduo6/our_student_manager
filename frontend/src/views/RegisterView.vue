﻿﻿﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">注册</h1>
      <p class="auth-subtitle">创建学生账号</p>

      <div v-if="errorMsg" class="message message-error">{{ errorMsg }}</div>
      <div v-if="successMsg" class="message message-success">{{ successMsg }}</div>

      <form @submit.prevent="handleRegister">
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
            placeholder="请输入密码（至少6位）"
            required
            minlength="6"
            autocomplete="new-password"
          />
        </div>

        <div class="form-group">
          <label class="form-label" for="name">姓名</label>
          <input
            id="name"
            v-model="form.name"
            class="form-input"
            type="text"
            placeholder="请输入真实姓名"
            required
          />
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg" :disabled="loading">
          <span v-if="loading" class="spinner" style="width:1rem;height:1rem;border-width:2px;"></span>
          <span v-else>注册</span>
        </button>
      </form>

      <p class="auth-footer">
        已有账号？
        <router-link to="/login">立即登录</router-link>
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
  name: ''
})
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

async function handleRegister() {
  errorMsg.value = ''
  successMsg.value = ''
  if (form.password.length < 6) {
    errorMsg.value = '密码长度至少为6位'
    return
  }
  loading.value = true
  try {
    await authStore.register(form.username, form.password, form.name)
    successMsg.value = '注册成功！即将跳转到登录页...'
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (e) {
    errorMsg.value = e.displayMessage || e.displayMessage || e.message || '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-footer {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.875rem;
  color: var(--gray-500);
}
</style>
