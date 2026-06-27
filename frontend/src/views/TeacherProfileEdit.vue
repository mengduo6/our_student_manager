<template>
  <div class="page-container">
    <div class="page-card">
      <h2 class="page-title">编辑教师信息</h2>
      <p class="page-desc">修改您的职业相关信息。部分字段（如用户名）不可直接修改。</p>

      <form class="form" @submit.prevent="handleSubmit">
        <!-- Read-only fields -->
        <div class="form-group">
          <label class="form-label">用户名 <span class="label-tag">不可修改</span></label>
          <input type="text" class="form-input readonly" :value="form.username" disabled />
        </div>

        <div class="form-group">
          <label class="form-label">当前状态 <span class="label-tag">不可修改</span></label>
          <input type="text" class="form-input readonly" :value="statusLabel" disabled />
        </div>

        <!-- Editable fields -->
        <div class="form-group">
          <label class="form-label required">姓名</label>
          <input
            type="text"
            class="form-input"
            v-model="form.name"
            placeholder="请输入姓名"
            maxlength="50"
            required
          />
          <span v-if="errors.name" class="form-error">{{ errors.name }}</span>
        </div>

        <div class="form-group">
          <label class="form-label">所属院系</label>
          <input
            type="text"
            class="form-input"
            v-model="form.department"
            placeholder="请输入所属院系"
            maxlength="100"
          />
          <span v-if="errors.department" class="form-error">{{ errors.department }}</span>
        </div>

        <div class="form-group">
          <label class="form-label">职称</label>
          <input
            type="text"
            class="form-input"
            v-model="form.title"
            placeholder="例: 教授、副教授、讲师"
            maxlength="50"
          />
          <span v-if="errors.title" class="form-error">{{ errors.title }}</span>
        </div>

        <div class="form-group">
          <label class="form-label">电子邮箱</label>
          <input
            type="email"
            class="form-input"
            v-model="form.email"
            placeholder="example@domain.com"
            maxlength="100"
          />
          <span v-if="errors.email" class="form-error">{{ errors.email }}</span>
        </div>

        <div class="form-group">
          <label class="form-label">联系电话</label>
          <input
            type="tel"
            class="form-input"
            v-model="form.phone"
            placeholder="请输入联系电话"
            maxlength="20"
          />
          <span v-if="errors.phone" class="form-error">{{ errors.phone }}</span>
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="submitting">
            {{ submitting ? '保存中...' : '保存修改' }}
          </button>
          <router-link to="/teacher" class="btn btn-outline">取消</router-link>
        </div>

        <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>
        <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import authStore from '../stores/auth'
import { getTeacherInfo, updateTeacherProfile } from '../api/teacher'

const router = useRouter()
const auth = authStore

const form = reactive({
  username: '',
  name: '',
  department: '',
  title: '',
  email: '',
  phone: '',
  status: 0
})

const errors = reactive({})
const submitting = ref(false)
const successMsg = ref('')
const errorMsg = ref('')
const originalForm = ref({})

const statusLabel = computed(() => {
  const map = { 0: '在职', 1: '超级管理员', 2: '退休' }
  return map[form.status] || '未知'
})

onMounted(async () => {
  try {
    const res = await getTeacherInfo()
    const data = res.data || res
    form.username = data.username || ''
    form.name = data.name || ''
    form.department = data.department || ''
    form.title = data.title || ''
    form.email = data.email || ''
    form.phone = data.phone || ''
    form.status = data.status || 0
    originalForm.value = {
      name: form.name,
      department: form.department,
      title: form.title,
      email: form.email,
      phone: form.phone
    }
  } catch (e) {
    errorMsg.value = '加载教师信息失败: ' + (e.displayMessage || e.message || '网络错误')
  }
})

function validate() {
  Object.keys(errors).forEach(k => delete errors[k])
  let valid = true

  if (!form.name || !form.name.trim()) {
    errors.name = '姓名不能为空'
    valid = false
  }
  if (form.name && form.name.length > 50) {
    errors.name = '姓名不能超过50个字符'
    valid = false
  }
  if (form.department && form.department.length > 100) {
    errors.department = '院系名称不能超过100个字符'
    valid = false
  }
  if (form.title && form.title.length > 50) {
    errors.title = '职称不能超过50个字符'
    valid = false
  }
  if (form.email && !/^[\w.+-]+@[\w-]+\.[\w.]+$/.test(form.email)) {
    errors.email = '邮箱格式不正确'
    valid = false
  }
  if (form.phone && !/^[\d\-+() ]{7,20}$/.test(form.phone)) {
    errors.phone = '电话号码格式不正确'
    valid = false
  }
  return valid
}

function isChanged() {
  const orig = originalForm.value
  return form.name !== orig.name ||
         form.department !== orig.department ||
         form.title !== orig.title ||
         form.email !== orig.email ||
         form.phone !== orig.phone
}

async function handleSubmit() {
  errorMsg.value = ''
  successMsg.value = ''

  if (!validate()) return
  if (!isChanged()) {
    errorMsg.value = '未检测到任何修改'
    return
  }

  submitting.value = true
  try {
    const payload = {}
    if (form.name !== originalForm.value.name) payload.name = form.name.trim()
    if (form.department !== (originalForm.value.department || '')) payload.department = form.department ? form.department.trim() : ''
    if (form.title !== (originalForm.value.title || '')) payload.title = form.title ? form.title.trim() : ''
    if (form.email !== (originalForm.value.email || '')) payload.email = form.email ? form.email.trim() : ''
    if (form.phone !== (originalForm.value.phone || '')) payload.phone = form.phone ? form.phone.trim() : ''

    const res = await updateTeacherProfile(payload)
    if (payload.name && auth.user) {
      auth.user.name = payload.name
      auth.user = { ...auth.user }
    }
    originalForm.value = {
      name: form.name,
      department: form.department,
      title: form.title,
      email: form.email,
      phone: form.phone
    }
    successMsg.value = '信息更新成功！'
    setTimeout(() => { router.push('/teacher') }, 1500)
  } catch (e) {
    errorMsg.value = '保存失败: ' + (e.displayMessage || e.message || '网络错误，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-container { padding: 2rem 1rem; max-width: 640px; margin: 0 auto; }
.page-card {
  background: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  padding: 2rem;
}
.page-title { font-size: 1.25rem; font-weight: 700; color: var(--gray-900); margin-bottom: 0.25rem; }
.page-desc { font-size: 0.875rem; color: var(--gray-500); margin-bottom: 1.5rem; }
.form { display: flex; flex-direction: column; gap: 1.25rem; }
.form-group { display: flex; flex-direction: column; gap: 0.375rem; }
.form-label { font-size: 0.8125rem; font-weight: 600; color: var(--gray-700); display: flex; align-items: center; gap: 0.5rem; }
.form-label.required::after { content: ' *'; color: #ef4444; }
.label-tag {
  font-size: 0.6875rem;
  background: var(--gray-100);
  color: var(--gray-500);
  padding: 1px 6px;
  border-radius: 3px;
  font-weight: 400;
}
.form-input {
  padding: 0.625rem 0.875rem;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius);
  font-size: 0.875rem;
  color: var(--gray-900);
  transition: border-color 0.2s;
  background: var(--white);
}
.form-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-light); }
.form-input.readonly { background: var(--gray-50); color: var(--gray-500); cursor: not-allowed; }
.form-error { font-size: 0.75rem; color: #ef4444; }
.form-actions { display: flex; gap: 0.75rem; margin-top: 0.5rem; }
.alert {
  padding: 0.75rem 1rem;
  border-radius: var(--radius);
  font-size: 0.875rem;
}
.alert-success { background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; }
.alert-error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
.btn { padding: 0.625rem 1.25rem; font-size: 0.875rem; border-radius: var(--radius); cursor: pointer; border: none; font-weight: 500; transition: all 0.2s; text-decoration: none; display: inline-flex; align-items: center; justify-content: center; }
.btn-primary { background: var(--primary); color: var(--white); }
.btn-primary:hover { background: var(--primary-dark); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-outline { background: transparent; color: var(--gray-600); border: 1px solid var(--gray-300); }
.btn-outline:hover { background: var(--gray-50); }

@media (max-width: 480px) {
  .page-card { padding: 1.25rem; }
}
</style>
