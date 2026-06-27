<template>
  <div class="page-wrapper">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="page-title">成绩查询</h1>
          <p class="page-subtitle">查看所有课程成绩与绩点</p>
        </div>
        <router-link to="/student" class="btn btn-outline">← 返回</router-link>
      </div>

      <!-- Summary Stats -->
      <div class="stat-cards" v-if="grades.length > 0">
        <div class="stat-card">
          <div class="stat-value">{{ gpa }}</div>
          <div class="stat-label">GPA</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ averageScore }}</div>
          <div class="stat-label">平均分</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ maxScore }}</div>
          <div class="stat-label">最高分</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ passedCount }}/{{ grades.length }}</div>
          <div class="stat-label">通过/总课程</div>
        </div>
      </div>

      <!-- Grade Table -->
      <div v-if="loading" class="loading"><span class="spinner"></span></div>
      <div v-else-if="grades.length === 0" class="empty-state">
        <div class="empty-state-icon">📊</div>
        <div class="empty-state-text">暂无成绩记录</div>
      </div>
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>课程名称</th>
              <th>课程编号</th>
              <th>授课教师</th>
              <th>成绩</th>
              <th>等级</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in grades" :key="item.courseId">
              <td>{{ item.courseName || item.subject || item.name }}</td>
              <td>{{ item.courseCode || item.courseId || item.code || '-' }}</td>
              <td>{{ item.teacherName || '-' }}</td>
              <td>
                <span class="score" :class="scoreClass(item.score)">{{ item.score ?? item.grade ?? '-' }}</span>
              </td>
              <td>{{ gradeLetter(item.score) }}</td>
              <td>
                <span class="badge" :class="item.score >= 60 ? 'badge-success' : 'badge-danger'">
                  {{ item.score >= 60 ? '通过' : '未通过' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStudentGrades } from '../api/student'

const grades = ref([])
const loading = ref(true)

const gpa = computed(() => {
  if (grades.value.length === 0) return '--'
  const scoreToPoint = (s) => {
    if (s >= 95) return 4.0
    if (s >= 90) return 4.0
    if (s >= 85) return 3.7
    if (s >= 82) return 3.3
    if (s >= 78) return 3.0
    if (s >= 75) return 2.7
    if (s >= 72) return 2.3
    if (s >= 68) return 2.0
    if (s >= 64) return 1.5
    if (s >= 60) return 1.0
    return 0
  }
  const totalPoints = grades.value.reduce((sum, g) => {
    return sum + scoreToPoint(g.score ?? g.grade ?? 0)
  }, 0)
  return (totalPoints / grades.value.length).toFixed(2)
})

const averageScore = computed(() => {
  if (grades.value.length === 0) return '--'
  const sum = grades.value.reduce((acc, g) => acc + (g.score ?? g.grade ?? 0), 0)
  return (sum / grades.value.length).toFixed(1)
})

const maxScore = computed(() => {
  if (grades.value.length === 0) return '--'
  return Math.max(...grades.value.map(g => g.score ?? g.grade ?? 0))
})

const passedCount = computed(() => {
  return grades.value.filter(g => (g.score ?? g.grade ?? 0) >= 60).length
})

function scoreClass(score) {
  if (score == null) return ''
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

function gradeLetter(score) {
  if (score == null) return '-'
  if (score >= 95) return 'A+'
  if (score >= 90) return 'A'
  if (score >= 85) return 'B+'
  if (score >= 80) return 'B'
  if (score >= 75) return 'C+'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

onMounted(async () => {
  try {
    const res = await getStudentGrades()
    grades.value = res.data || res || []
  } catch (e) {
    console.error('Failed to load grades:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.score {
  font-weight: 700;
  font-size: 1rem;
}

.score-excellent { color: var(--success); }
.score-good { color: var(--primary); }
.score-pass { color: var(--warning); }
.score-fail { color: var(--danger); }
</style>
