﻿﻿﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="page-wrapper">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="page-title">选课中心</h1>
          <p class="page-subtitle">浏览并选择您感兴趣的课程</p>
        </div>
        <router-link to="/student" class="btn btn-outline">← 返回</router-link>
      </div>

      <div v-if="message" class="message" :class="messageClass">{{ message }}</div>

      <div v-if="loading" class="loading"><span class="spinner"></span></div>

      <template v-else>
        <div v-if="courses.length === 0" class="empty-state">
          <div class="empty-state-icon">📖</div>
          <div class="empty-state-text">暂无可选课程</div>
        </div>
        <div v-else class="grid grid-2">
          <StudentCourseCard
            v-for="course in courses"
            :key="course.id"
            :course="course"
            @enroll="handleEnroll"
            @drop="handleDrop"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllAvailableCourses } from '../api/course'
import { getStudentCourses, enrollCourse, dropCourse } from '../api/student'
import StudentCourseCard from '../components/StudentCourseCard.vue'

const courses = ref([])
const loading = ref(true)
const message = ref('')
const messageClass = ref('message-success')

onMounted(async () => {
  await loadCourses()
})

async function loadCourses() {
  loading.value = true
  try {
    const [allRes, enrolledRes] = await Promise.all([
      getAllAvailableCourses(),
      getStudentCourses()
    ])

    // Normalize course data — backend uses courseId/subject, frontend components use id/name
    function normalize(c) {
      return {
        ...c,
        id: c.courseId || c.id,
        name: c.subject || c.name
      }
    }

    const allCourses = (allRes.data || allRes || []).map(c => normalize({ ...c, enrolled: false }))
    const enrolledCourses = (enrolledRes.data || enrolledRes || []).map(normalize)
    const enrolledIds = new Set(enrolledCourses.map(c => c.id || c.courseId))

    courses.value = allCourses.map(c => ({
      ...c,
      enrolled: enrolledIds.has(c.id || c.courseId)
    }))
  } catch (e) {
    console.error('Failed to load courses:', e)
    showMessage('加载课程失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  } finally {
    loading.value = false
  }
}

async function handleEnroll(course) {
  try {
    await enrollCourse(course.id)
    course.enrolled = true
    showMessage(`成功选课: ${course.name}`, 'message-success')
  } catch (e) {
    showMessage('选课失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  }
}

async function handleDrop(course) {
  if (!confirm(`确定要退选「${course.name}」吗？`)) return
  try {
    await dropCourse(course.id)
    course.enrolled = false
    showMessage(`已退选: ${course.name}`, 'message-info')
  } catch (e) {
    showMessage('退课失败: ' + (e.displayMessage || e.message || '未知错误'), 'message-error')
  }
}

function showMessage(msg, cls) {
  message.value = msg
  messageClass.value = cls
  setTimeout(() => { message.value = '' }, 3000)
}
</script>
