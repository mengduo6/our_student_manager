<template>
  <div class="page-wrapper">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">班级管理（班长）</h1>
        <span class="page-subtitle">{{ studentInfo.className || '未分配班级' }}</span>
      </div>

      <div v-if="message" class="message" :class="messageClass">{{ message }}</div>

      <div v-if="loading" class="loading"><span class="spinner"></span> 加载中...</div>

      <div v-else-if="classmates.length === 0" class="empty-state">
        <div class="empty-state-icon">👥</div>
        <div class="empty-state-text">暂未获取到班级同学信息</div>
      </div>

      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>学号</th>
              <th>姓名</th>
              <th>专业</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="student in classmates" :key="student.id">
              <td>{{ student.id }}</td>
              <td>
                <template v-if="editingId === student.id">
                  <input v-model="editForm.name" class="form-input" style="width:100px" />
                </template>
                <template v-else>{{ student.name }}</template>
              </td>
              <td>
                <template v-if="editingId === student.id">
                  <input v-model="editForm.major" class="form-input" style="width:140px" />
                </template>
                <template v-else>{{ student.major || '-' }}</template>
              </td>
              <td>
                <span class="badge" :class="student.status === '班长' ? 'badge-warning' : student.status === '休学' ? 'badge-danger' : 'badge-success'">
                  {{ student.status }}
                </span>
              </td>
              <td class="action-cell">
                <template v-if="editingId === student.id">
                  <button class="btn btn-primary btn-sm" @click="saveEdit(student.id)">保存</button>
                  <button class="btn btn-outline btn-sm" @click="cancelEdit()">取消</button>
                </template>
                <template v-else>
                  <button
                    class="btn btn-outline btn-sm"
                    :disabled="student.status === '休学'"
                    @click="startEdit(student)"
                  >编辑</button>
                </template>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getClassmates, updateStudentInfo } from '../api/monitor'
import authStore from '../stores/auth'

const classmates = ref([])
const loading = ref(true)
const message = ref('')
const messageClass = ref('message-success')
const editingId = ref(null)
const editForm = reactive({ name: '', major: '' })

const studentInfo = computed(() => {
  const u = authStore.user || {}
  return {
    className: u.className || u.className_ || null
  }
})

onMounted(async () => {
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    const user = authStore.user || {}
    const classId = user.classId || (await fetchMyClassId())

    if (!classId) {
      message.value = '无法获取班级信息，请联系超级管理员'
      messageClass.value = 'message-error'
      classmates.value = []
      return
    }

    const res = await getClassmates(classId)
    classmates.value = res.data || res || []
  } catch (e) {
    showMessage('加载失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally {
    loading.value = false
  }
}

async function fetchMyClassId() {
  try {
    const infoRes = await import('../api/student').then(m => m.getStudentInfo())
    const data = infoRes.data || infoRes
    return data.classId
  } catch {
    return null
  }
}

function startEdit(student) {
  editingId.value = student.id
  editForm.name = student.name || ''
  editForm.major = student.major || ''
}

function cancelEdit() {
  editingId.value = null
}

async function saveEdit(studentId) {
  try {
    await updateStudentInfo(studentId, {
      name: editForm.name.trim(),
      major: editForm.major.trim()
    })
    showMessage('信息更新成功', 'message-success')
    editingId.value = null
    await loadData()
  } catch (e) {
    showMessage('更新失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  }
}

function showMessage(msg, cls) {
  message.value = msg
  messageClass.value = cls
  setTimeout(() => { message.value = '' }, 3000)
}
</script>
