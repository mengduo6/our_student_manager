﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="page-wrapper">
    <div class="container">
      <!-- Teacher Info -->
      <div class="card mb-4" v-if="teacherInfo">
        <div class="card-header">教师信息</div>
        <div class="info-grid">
          <div class="info-item">
            <div class="info-label">姓名</div>
            <div class="info-value">{{ teacherInfo.name }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">工号</div>
            <div class="info-value">{{ teacherInfo.teacherNo || teacherInfo.id }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">职称</div>
            <div class="info-value">{{ teacherInfo.title || '未设置' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">部门</div>
            <div class="info-value">{{ teacherInfo.department || '未设置' }}</div>
          </div>
        </div>
      </div>

      <!-- Course Management -->
      <div class="page-header">
        <h1 class="page-title">我的课程 ({{ courses.length }})</h1>
        <button class="btn btn-primary" @click="openCreateModal">+ 新建课程</button>
      </div>

      <div v-if="message" class="message" :class="messageClass">{{ message }}</div>

      <div v-if="loading" class="loading"><span class="spinner"></span></div>
      <div v-else-if="courses.length === 0" class="empty-state">
        <div class="empty-state-icon">📖</div>
        <div class="empty-state-text">还没有创建课程，点击上方按钮添加</div>
      </div>
      <div v-else class="grid grid-2">
        <TeacherCourseCard
          v-for="course in courses"
          :key="course.id"
          :course="course"
          @edit="openEditModal"
          @delete="handleDeleteCourse"
          @viewStudents="openStudentModal"
        />
      </div>

      <!-- Create/Edit Course Modal -->
      <div class="modal-overlay" v-if="showCourseModal" @click.self="showCourseModal = false">
        <div class="modal">
          <h3 class="modal-title">{{ editingCourse ? '编辑课程' : '新建课程' }}</h3>
          <div class="form-group">
            <label class="form-label">课程名称 <span class="required">*</span></label>
            <input v-model="courseForm.subject" class="form-input" placeholder="请输入课程名称" required />
          </div>
          <div class="form-group">
            <label class="form-label">课程描述</label>
            <textarea v-model="courseForm.about" class="form-input" rows="4" placeholder="课程描述（可选）"></textarea>
          </div>
          <div class="modal-actions">
            <button class="btn btn-outline" @click="showCourseModal = false">取消</button>
            <button class="btn btn-primary" @click="submitCourseForm" :disabled="submitting">
              {{ submitting ? '提交中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Students in Course Modal -->
      <div class="modal-overlay" v-if="showStudentModal" @click.self="showStudentModal = false">
        <div class="modal" style="max-width: 720px;">
          <h3 class="modal-title">{{ selectedCourse?.name }} - 学生列表 ({{ enrolledStudents.length }})</h3>

          <div v-if="studentLoading" class="loading"><span class="spinner"></span></div>
          <div v-else-if="enrolledStudents.length === 0" class="empty-state" style="padding:2rem 0;">
            <div class="empty-state-text">暂无学生选课</div>
          </div>
          <div v-else>
            <div class="table-wrapper" style="box-shadow:none;border:none;">
              <table>
                <thead>
                  <tr>
                    <th>姓名</th>
                    <th>学号</th>
                    <th>成绩</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="student in enrolledStudents" :key="student.id">
                    <td>{{ student.name }}</td>
                    <td>{{ student.studentNo || student.id }}</td>
                    <td>
                      <div class="grade-edit">
                        <input
                          v-model="student.editGrade"
                          class="grade-input"
                          type="number"
                          min="0"
                          max="100"
                          placeholder="输入成绩"
                        />
                        <button
                          class="btn btn-primary btn-sm"
                          @click="updateGrade(student)"
                          :disabled="student.updating"
                        >更新</button>
                      </div>
                    </td>
                    <td>
                      <button
                        class="btn btn-danger btn-sm"
                        @click="removeStudent(student)"
                        :disabled="student.removing"
                      >移除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="modal-actions">
            <button class="btn btn-outline" @click="showStudentModal = false">关闭</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  getTeacherInfo,
  getTeacherCourses,
  createCourse,
  updateCourse,
  deleteCourse,
  getCourseStudents,
  updateStudentGrade,
  removeStudentFromCourse
} from '../api/teacher'
import TeacherCourseCard from '../components/TeacherCourseCard.vue'

const teacherInfo = ref(null)
const courses = ref([])
const loading = ref(true)
const message = ref('')
const messageClass = ref('message-success')
const submitting = ref(false)

// Course modal
const showCourseModal = ref(false)
const editingCourse = ref(null)
const courseForm = reactive({
  subject: '',
  about: ''
})

// Student modal
const showStudentModal = ref(false)
const selectedCourse = ref(null)
const enrolledStudents = ref([])
const studentLoading = ref(false)

onMounted(async () => {
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    const [infoRes, coursesRes] = await Promise.all([
      getTeacherInfo(),
      getTeacherCourses()
    ])
    teacherInfo.value = infoRes.data || infoRes
    courses.value = (coursesRes.data || coursesRes || [])
  } catch (e) {
    console.error('Failed to load teacher data:', e)
  } finally {
    loading.value = false
  }
}

function resetCourseForm() {
  courseForm.subject = ''
  courseForm.about = ''
}

function openCreateModal() {
  editingCourse.value = null
  resetCourseForm()
  showCourseModal.value = true
}

function openEditModal(course) {
  editingCourse.value = course
  courseForm.subject = course.name || course.subject || ''
  courseForm.about = course.about || course.description || ''
  showCourseModal.value = true
}

async function submitCourseForm() {
  if (!courseForm.subject.trim()) {
    showMessage('课程名称不能为空', 'message-error')
    return
  }
  submitting.value = true
  try {
    const data = { subject: courseForm.subject.trim(), about: courseForm.about.trim() }
    if (editingCourse.value) {
      await updateCourse(editingCourse.value.id, data)
      showMessage('课程更新成功', 'message-success')
    } else {
      await createCourse(data)
      showMessage('课程创建成功', 'message-success')
    }
    showCourseModal.value = false
    await loadData()
  } catch (e) {
    showMessage('操作失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally {
    submitting.value = false
  }
}

async function handleDeleteCourse(course) {
  if (!confirm(`确定要删除课程「${course.name}」吗？此操作不可撤销。`)) return
  try {
    await deleteCourse(course.id)
    showMessage('课程已删除', 'message-success')
    await loadData()
  } catch (e) {
    showMessage('删除失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  }
}

async function openStudentModal(course) {
  selectedCourse.value = course
  showStudentModal.value = true
  studentLoading.value = true
  enrolledStudents.value = []
  try {
    const res = await getCourseStudents(course.id)
    const list = (res.data || res || []).map(s => ({
      ...s,
      editGrade: s.score ?? s.grade ?? '',
      updating: false,
      removing: false
    }))
    enrolledStudents.value = list
  } catch (e) {
    showMessage('加载学生列表失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally {
    studentLoading.value = false
  }
}

async function updateGrade(student) {
  const val = parseFloat(student.editGrade)
  if (isNaN(val) || val < 0 || val > 100) {
    showMessage('请输入0-100的有效成绩', 'message-error')
    return
  }
  student.updating = true
  try {
    await updateStudentGrade(selectedCourse.value.id, student.id, { score: val })
    student.score = val
    showMessage('成绩更新成功', 'message-success')
  } catch (e) {
    showMessage('更新成绩失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally {
    student.updating = false
  }
}

async function removeStudent(student) {
  if (!confirm(`确定要将 ${student.name} 从课程中移除吗？`)) return
  student.removing = true
  try {
    await removeStudentFromCourse(selectedCourse.value.id, student.id)
    enrolledStudents.value = enrolledStudents.value.filter(s => s.id !== student.id)
    const idx = courses.value.findIndex(c => c.id === selectedCourse.value.id)
    if (idx >= 0 && courses.value[idx].enrollmentCount != null) {
      courses.value[idx].enrollmentCount = Math.max(0, courses.value[idx].enrollmentCount - 1)
    }
    showMessage('学生已移除', 'message-success')
  } catch (e) {
    showMessage('移除失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally {
    student.removing = false
  }
}

function showMessage(msg, cls) {
  message.value = msg
  messageClass.value = cls
  setTimeout(() => { message.value = '' }, 3000)
}
</script>

<style scoped>
.grade-edit {
  display: flex;
  align-items: center;
  gap: 0.375rem;
}

.grade-input {
  width: 80px;
  padding: 0.375rem 0.5rem;
  border: 1px solid var(--gray-300);
  border-radius: var(--radius-sm);
  text-align: center;
  outline: none;
}

.grade-input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.15);
}
</style>
