<template>
  <div class="page-wrapper">
    <div class="container">
      <!-- Student Info Card -->
      <div class="card mb-4" v-if="studentInfo">
        <div class="card-header">个人信息</div>
        <div class="info-grid">
          <div class="info-item">
            <div class="info-label">姓名</div>
            <div class="info-value">{{ studentInfo.name }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">学号</div>
            <div class="info-value">{{ studentInfo.studentNo || studentInfo.id }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">专业</div>
            <div class="info-value">{{ studentInfo.major || '未设置' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">班级</div>
            <div class="info-value">{{ studentInfo.className || '未分配' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">状态</div>
            <div class="info-value">
              <span class="badge" :class="studentInfo.status === 'ACTIVE' || studentInfo.status === '在读' ? 'badge-success' : 'badge-danger'">
                {{ studentInfo.status === 'ACTIVE' || studentInfo.status === '在读' ? '在读' : '休学' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="action-bar">
        <router-link to="/student/courses" class="btn btn-primary">📖 选课中心</router-link>
        <router-link to="/student/grades" class="btn btn-outline">📊 查看成绩详情</router-link>
      </div>

      <!-- Enrolled Courses -->
      <div class="section">
        <h3 class="section-title">已选课程 ({{ courses.length }})</h3>
        <div v-if="loading" class="loading"><span class="spinner"></span></div>
        <div v-else-if="courses.length === 0" class="empty-state">
          <div class="empty-state-icon">📚</div>
          <div class="empty-state-text">还没有选课，快去选课中心看看吧</div>
        </div>
        <div v-else class="grid grid-2">
          <StudentCourseCard
            v-for="course in courses"
            :key="course.id"
            :course="course"
            :readonly="true"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentInfo, getStudentCourses } from '../api/student'
import StudentCourseCard from '../components/StudentCourseCard.vue'

const studentInfo = ref(null)
const courses = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const [infoRes, coursesRes] = await Promise.all([
      getStudentInfo(),
      getStudentCourses()
    ])
    studentInfo.value = infoRes.data || infoRes
    courses.value = (coursesRes.data || coursesRes || []).map(c => ({
      ...c,
      id: c.courseId || c.id,
      name: c.subject || c.name,
      enrolled: true,
      score: c.score ?? c.grade
    }))
  } catch (e) {
    console.error('Failed to load student data:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.action-bar {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  margin-bottom: 2rem;
}

.section {
  margin-top: 0.5rem;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 1rem;
}
</style>
