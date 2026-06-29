﻿﻿﻿﻿<template>
  <div class="page-wrapper">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">系统管理</h1>
      </div>

      <div v-if="message" class="message" :class="messageClass">{{ message }}</div>

      <!-- Tabs -->
      <div class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="tab"
          :class="{ active: activeTab === tab.key }"
          @click="switchTab(tab.key)"
        >{{ tab.label }}</button>
      </div>

      <AdminPanel
        :activeTab="activeTab"
        :students="students"
        :teachers="teachers"
        :classes="classList"
        :courses="adminCourses"
        :loading="loading"
        :selectingStudents="selectingStudents"
        :selectingTeachers="selectingTeachers"
        @openCreateStudent="openStudentModal()"
        @openEditStudent="openStudentModal($event)"
        @updateStudentStatus="handleStudentStatus"
        @appointMonitor="handleAppointMonitor"
        @dismissMonitor="handleDismissMonitor"
        @deleteStudent="handleDeleteStudent"
        @batchDeleteStudents="handleBatchDeleteStudents"
        @openCreateTeacher="openTeacherModal()"
        @openEditTeacher="openTeacherModal($event)"
        @updateTeacherStatus="handleTeacherStatus"
        @deleteTeacher="handleDeleteTeacher"
        @batchDeleteTeachers="handleBatchDeleteTeachers"
        @openCreateClass="openClassModal()"
        @openEditClass="openClassModalEdit($event)"
        @deleteCourse="handleDeleteCourse"
        @toggleSelectStudents="selectingStudents = !selectingStudents"
        @toggleSelectTeachers="selectingTeachers = !selectingTeachers"
      />

      <!-- =========== Student Modal =========== -->
      <div class="modal-overlay" v-if="showStudentModal" @click.self="showStudentModal = false">
        <div class="modal modal-wide">
          <h3 class="modal-title">{{ editingStudent ? '编辑学生' : '新建学生' }}</h3>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">用户名 <span class="req">*</span></label>
              <input v-model="studentForm.username" class="form-input" placeholder="登录用户名" :disabled="!!editingStudent" />
            </div>
            <div class="form-group">
              <label class="form-label">姓名 <span class="req">*</span></label>
              <input v-model="studentForm.name" class="form-input" placeholder="真实姓名" />
            </div>
          </div>
          <div class="form-group" v-if="!editingStudent">
            <label class="form-label">密码</label>
            <input v-model="studentForm.password" class="form-input" type="text" placeholder="默认 123456" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">专业</label>
              <input v-model="studentForm.major" class="form-input" placeholder="专业名称" />
            </div>
            <div class="form-group">
              <label class="form-label">班级</label>
              <select v-model="studentForm.classId" class="form-input">
                <option :value="null">未分配</option>
                <option v-for="cls in classList" :key="cls.id" :value="cls.id">{{ cls.name }}</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">邮箱</label>
              <input v-model="studentForm.email" class="form-input" placeholder="example@mail.com" />
            </div>
            <div class="form-group">
              <label class="form-label">电话</label>
              <input v-model="studentForm.phone" class="form-input" placeholder="手机号码" />
            </div>
          </div>
          <div class="modal-actions">
            <button class="btn btn-outline" @click="showStudentModal = false">取消</button>
            <button class="btn btn-primary" @click="submitStudentForm" :disabled="submitting">
              {{ submitting ? '保存中...' : (editingStudent ? '保存修改' : '创建') }}
            </button>
          </div>
        </div>
      </div>

      <!-- =========== Teacher Modal =========== -->
      <div class="modal-overlay" v-if="showTeacherModal" @click.self="showTeacherModal = false">
        <div class="modal modal-wide">
          <h3 class="modal-title">{{ editingTeacher ? '编辑教师' : '新建教师' }}</h3>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">用户名 <span class="req">*</span></label>
              <input v-model="teacherForm.username" class="form-input" placeholder="登录用户名" :disabled="!!editingTeacher" />
            </div>
            <div class="form-group">
              <label class="form-label">姓名 <span class="req">*</span></label>
              <input v-model="teacherForm.name" class="form-input" placeholder="真实姓名" />
            </div>
          </div>
          <div class="form-group" v-if="!editingTeacher">
            <label class="form-label">密码</label>
            <input v-model="teacherForm.password" class="form-input" type="text" placeholder="默认 123456" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">院系</label>
              <input v-model="teacherForm.department" class="form-input" placeholder="所属院系" />
            </div>
            <div class="form-group">
              <label class="form-label">职称</label>
              <input v-model="teacherForm.title" class="form-input" placeholder="教授/副教授/讲师" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">邮箱</label>
              <input v-model="teacherForm.email" class="form-input" placeholder="example@mail.com" />
            </div>
            <div class="form-group">
              <label class="form-label">电话</label>
              <input v-model="teacherForm.phone" class="form-input" placeholder="手机号码" />
            </div>
          </div>
          <div class="modal-actions">
            <button class="btn btn-outline" @click="showTeacherModal = false">取消</button>
            <button class="btn btn-primary" @click="submitTeacherForm" :disabled="submitting">
              {{ submitting ? '保存中...' : (editingTeacher ? '保存修改' : '创建') }}
            </button>
          </div>
        </div>
      </div>

      <!-- =========== Class Modal =========== -->
      <div class="modal-overlay" v-if="showClassModal" @click.self="showClassModal = false">
        <div class="modal">
          <h3 class="modal-title">{{ editingClass ? '编辑班级' : '新建班级' }}</h3>
          <div class="form-group">
            <label class="form-label">班级名称 <span class="req">*</span></label>
            <input v-model="classForm.name" class="form-input" placeholder="如: 计算机科学2025-1班" />
          </div>
          <div class="modal-actions">
            <button class="btn btn-outline" @click="showClassModal = false">取消</button>
            <button class="btn btn-primary" @click="submitClassForm" :disabled="submitting">
              {{ submitting ? '提交中...' : (editingClass ? '保存修改' : '创建') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  getAllStudents, createStudent, updateStudent, deleteStudent, updateStudentStatus, appointMonitor, dismissMonitor, batchDeleteStudents,
  getAllTeachers, createTeacher, updateTeacher, deleteTeacher, updateTeacherStatus, batchDeleteTeachers,
  getAllClasses, createClass, updateClass,
  getAllCourses, deleteAdminCourse
} from '../api/admin'
import AdminPanel from '../components/AdminPanel.vue'

const tabs = [
  { key: 'students', label: '学生管理' },
  { key: 'teachers', label: '教师管理' },
  { key: 'classes', label: '班级管理' },
  { key: 'courses', label: '课程管理' }
]

const activeTab = ref('students')
const students = ref([])
const teachers = ref([])
const classList = ref([])
const adminCourses = ref([])
const loading = reactive({ students: false, teachers: false, classes: false, courses: false })
const message = ref('')
const messageClass = ref('message-success')
const submitting = ref(false)
const selectingStudents = ref(false)
const selectingTeachers = ref(false)

// Student modal
const showStudentModal = ref(false)
const editingStudent = ref(null)
const studentForm = reactive({ username: '', password: '', name: '', major: '', classId: null, email: '', phone: '' })

// Teacher modal
const showTeacherModal = ref(false)
const editingTeacher = ref(null)
const teacherForm = reactive({ username: '', password: '', name: '', department: '', title: '', email: '', phone: '' })

// Class modal
const showClassModal = ref(false)
const editingClass = ref(null)
const classForm = reactive({ name: '' })

onMounted(() => { loadTabData(activeTab.value) })

// ========== Navigation ==========

function switchTab(key) {
  activeTab.value = key
  message.value = ''
  selectingStudents.value = false
  selectingTeachers.value = false
  loadTabData(key)
}

async function loadTabData(key) {
  loadAll()
}

async function loadAll() {
  await Promise.allSettled([loadStudents(), loadTeachers(), loadClasses(), loadCourses()])
}

async function loadStudents() {
  loading.students = true
  try { const r = await getAllStudents(); students.value = r.data || r || [] } catch (e) { console.error(e) } finally { loading.students = false }
}
async function loadTeachers() {
  loading.teachers = true
  try { const r = await getAllTeachers(); teachers.value = r.data || r || [] } catch (e) { console.error(e) } finally { loading.teachers = false }
}
async function loadClasses() {
  loading.classes = true
  try { const r = await getAllClasses(); classList.value = r.data || r || [] } catch (e) { console.error(e) } finally { loading.classes = false }
}
async function loadCourses() {
  loading.courses = true
  try { const r = await getAllCourses(); adminCourses.value = r.data || r || [] } catch (e) { console.error(e) } finally { loading.courses = false }
}

// ========== Student CRUD ==========

function openStudentModal(student) {
  if (student) {
    editingStudent.value = student
    studentForm.username = student.username
    studentForm.name = student.name
    studentForm.major = student.major || ''
    studentForm.classId = student.classId || null
    studentForm.email = student.email || ''
    studentForm.phone = student.phone || ''
  } else {
    editingStudent.value = null
    studentForm.username = ''
    studentForm.password = ''
    studentForm.name = ''
    studentForm.major = ''
    studentForm.classId = null
    studentForm.email = ''
    studentForm.phone = ''
  }
  showStudentModal.value = true
}

async function submitStudentForm() {
  if (!studentForm.name.trim()) { showMsg('姓名不能为空', 'message-error'); return }
  if (!editingStudent.value && !studentForm.username.trim()) { showMsg('用户名不能为空', 'message-error'); return }

  const email = studentForm.email.trim()
  if (email && !/^[\w.+-]+@[\w-]+\.[\w.]+$/.test(email)) { showMsg('邮箱格式不正确', 'message-error'); return }

  submitting.value = true
  try {
    const payload = {
      name: studentForm.name.trim(),
      major: studentForm.major.trim() || null,
      classId: studentForm.classId || null,
      email: email || null,
      phone: studentForm.phone.trim() || null
    }

    if (editingStudent.value) {
      await updateStudent(editingStudent.value.id, payload)
      showMsg(`学生「${studentForm.name}」信息已更新`, 'message-success')
    } else {
      payload.username = studentForm.username.trim()
      payload.password = studentForm.password || undefined
      await createStudent(payload)
      showMsg(`学生「${studentForm.name}」创建成功`, 'message-success')
    }
    showStudentModal.value = false
    await loadStudents()
  } catch (e) {
    showMsg((editingStudent.value ? '更新' : '创建') + '失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally { submitting.value = false }
}

async function handleDeleteStudent(student) {
  if (!confirm(`确定要删除学生「${student.name}」吗？\n此操作不可撤销，将同时删除该学生的选课记录。`)) return
  try {
    await deleteStudent(student.id)
    showMsg(`学生「${student.name}」已删除`, 'message-success')
    await loadStudents()
  } catch (e) { showMsg('删除失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

async function handleBatchDeleteStudents(ids) {
  try {
    await batchDeleteStudents(ids)
    showMsg(`已批量删除 ${ids.length} 名学生`, 'message-success')
    selectingStudents.value = false
    await loadStudents()
  } catch (e) { showMsg('批量删除失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

// ========== Teacher CRUD ==========

function openTeacherModal(teacher) {
  if (teacher) {
    editingTeacher.value = teacher
    teacherForm.username = teacher.username
    teacherForm.name = teacher.name
    teacherForm.department = teacher.department || ''
    teacherForm.title = teacher.title || ''
    teacherForm.email = teacher.email || ''
    teacherForm.phone = teacher.phone || ''
  } else {
    editingTeacher.value = null
    teacherForm.username = ''
    teacherForm.password = ''
    teacherForm.name = ''
    teacherForm.department = ''
    teacherForm.title = ''
    teacherForm.email = ''
    teacherForm.phone = ''
  }
  showTeacherModal.value = true
}

async function submitTeacherForm() {
  if (!teacherForm.name.trim()) { showMsg('姓名不能为空', 'message-error'); return }
  if (!editingTeacher.value && !teacherForm.username.trim()) { showMsg('用户名不能为空', 'message-error'); return }

  const email = teacherForm.email.trim()
  if (email && !/^[\w.+-]+@[\w-]+\.[\w.]+$/.test(email)) { showMsg('邮箱格式不正确', 'message-error'); return }

  submitting.value = true
  try {
    const payload = {
      name: teacherForm.name.trim(),
      department: teacherForm.department.trim() || null,
      title: teacherForm.title.trim() || null,
      email: email || null,
      phone: teacherForm.phone.trim() || null
    }

    if (editingTeacher.value) {
      await updateTeacher(editingTeacher.value.id, payload)
      showMsg(`教师「${teacherForm.name}」信息已更新`, 'message-success')
    } else {
      payload.username = teacherForm.username.trim()
      payload.password = teacherForm.password || undefined
      await createTeacher(payload)
      showMsg(`教师「${teacherForm.name}」创建成功`, 'message-success')
    }
    showTeacherModal.value = false
    await loadTeachers()
  } catch (e) {
    showMsg((editingTeacher.value ? '更新' : '创建') + '失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally { submitting.value = false }
}

async function handleDeleteTeacher(teacher) {
  if (!confirm(`确定要删除教师「${teacher.name}」吗？\n此操作不可撤销，将同时删除该教师的所有课程。`)) return
  try {
    await deleteTeacher(teacher.id)
    showMsg(`教师「${teacher.name}」已删除`, 'message-success')
    await loadTeachers()
    await loadCourses()
  } catch (e) { showMsg('删除失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

async function handleBatchDeleteTeachers(ids) {
  try {
    await batchDeleteTeachers(ids)
    showMsg(`已批量删除 ${ids.length} 名教师`, 'message-success')
    selectingTeachers.value = false
    await loadTeachers()
    await loadCourses()
  } catch (e) { showMsg('批量删除失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

// ========== Student Status / Monitor ==========

async function handleStudentStatus(student, newStatus) {
  const labels = { 0: '在读', 2: '休学' }
  if (!confirm(`确定要将 ${student.name} 的状态改为「${labels[newStatus]}」吗？`)) return
  try {
    await updateStudentStatus(student.id, newStatus)
    showMsg(`学生 ${student.name} 状态已更新`, 'message-success')
    await loadStudents()
  } catch (e) { showMsg('操作失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

async function handleAppointMonitor(student) {
  if (!confirm(`确定要任命 ${student.name} 为班长吗？`)) return
  try {
    await appointMonitor(student.id)
    showMsg(`${student.name} 已被任命为班长`, 'message-success')
    await loadStudents()
    await loadClasses()
  } catch (e) { showMsg('操作失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

async function handleDismissMonitor(student) {
  if (!confirm(`确定要解除 ${student.name} 的班长职务吗？`)) return
  try {
    await dismissMonitor(student.id)
    showMsg(`${student.name} 班长职务已解除`, 'message-success')
    await loadStudents()
    await loadClasses()
  } catch (e) { showMsg('操作失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

async function handleTeacherStatus(teacher, newStatus) {
  const labels = { 0: '在职', 2: '退休' }
  if (!confirm(`确定要将 ${teacher.name} 的状态改为「${labels[newStatus]}」吗？`)) return
  try {
    await updateTeacherStatus(teacher.id, newStatus)
    showMsg(`教师 ${teacher.name} 状态已更新`, 'message-success')
    await loadTeachers()
  } catch (e) { showMsg('操作失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

// ========== Course ==========

async function handleDeleteCourse(course) {
  if (!confirm(`确定要删除课程「${course.name}」吗？此操作不可撤销。`)) return
  try {
    await deleteAdminCourse(course.id)
    showMsg(`课程「${course.name}」已删除`, 'message-success')
    await loadCourses()
  } catch (e) { showMsg('删除失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error') }
}

// ========== Class ==========

function openClassModal() {
  editingClass.value = null
  classForm.name = ''
  showClassModal.value = true
}

function openClassModalEdit(cls) {
  editingClass.value = cls
  classForm.name = cls.name
  showClassModal.value = true
}

async function submitClassForm() {
  if (!classForm.name.trim()) { showMsg('班级名称不能为空', 'message-error'); return }
  submitting.value = true
  try {
    if (editingClass.value) {
      await updateClass(editingClass.value.id, { classname: classForm.name.trim() })
      showMsg('班级名称已更新', 'message-success')
    } else {
      await createClass({ classname: classForm.name.trim() })
      showMsg('班级创建成功', 'message-success')
    }
    showClassModal.value = false
    await loadClasses()
  } catch (e) {
    showMsg((editingClass.value ? '更新' : '创建') + '失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally { submitting.value = false }
}

// ========== Helpers ==========

function showMsg(msg, cls) {
  message.value = msg
  messageClass.value = cls
  setTimeout(() => { message.value = '' }, 3000)
}
</script>

<style scoped>
.form-row {
  display: flex;
  gap: 1rem;
}
.form-row .form-group {
  flex: 1;
}
.req {
  color: #e53e3e;
}
@media (max-width: 480px) {
  .form-row {
    flex-direction: column;
  }
}
</style>
