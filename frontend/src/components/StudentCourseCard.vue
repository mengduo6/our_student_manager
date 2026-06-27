<template>
  <div class="course-card card">
    <div class="course-header">
      <h3 class="course-name">{{ course.name || course.subject }}</h3>
      <span class="badge" :class="statusClass">{{ statusLabel }}</span>
    </div>
    <div class="course-meta">
      <div class="meta-item">
        <span class="meta-label">课程编号</span>
        <span class="meta-value">{{ course.code || course.courseId || course.id }}</span>
      </div>
      <div class="meta-item">
        <span class="meta-label">授课教师</span>
        <span class="meta-value">{{ course.teacherName || '未指定' }}</span>
      </div>
      <div class="meta-item" v-if="course.credit != null">
        <span class="meta-label">学分</span>
        <span class="meta-value">{{ course.credit }}</span>
      </div>
      <div class="meta-item" v-if="course.semester">
        <span class="meta-label">学期</span>
        <span class="meta-value">{{ course.semester }}</span>
      </div>
    </div>
    <div class="course-grade" v-if="course.score != null || course.grade != null">
      <span class="grade-label">成绩</span>
      <span class="grade-value" :class="gradeClass">{{ course.score ?? course.grade }}</span>
    </div>
    <div class="course-footer" v-if="!readonly">
      <button
        v-if="!course.enrolled"
        class="btn btn-primary btn-sm"
        @click="$emit('enroll', course)"
      >
        选课
      </button>
      <button
        v-else
        class="btn btn-outline btn-sm"
        @click="$emit('drop', course)"
      >
        退课
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  course: { type: Object, required: true },
  readonly: { type: Boolean, default: false }
})

defineEmits(['enroll', 'drop'])

const statusLabel = computed(() => {
  return props.course.enrolled ? '已选' : '可选'
})

const statusClass = computed(() => {
  return props.course.enrolled ? 'badge-success' : 'badge-gray'
})

const gradeClass = computed(() => {
  const score = props.course.score ?? props.course.grade
  if (score == null) return ''
  if (score >= 90) return 'grade-excellent'
  if (score >= 80) return 'grade-good'
  if (score >= 60) return 'grade-pass'
  return 'grade-fail'
})
</script>

<style scoped>
.course-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.course-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
}

.course-name {
  font-size: 1.0625rem;
  font-weight: 600;
  color: var(--gray-900);
  margin: 0;
}

.course-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.meta-label {
  font-size: 0.75rem;
  color: var(--gray-400);
}

.meta-value {
  font-size: 0.875rem;
  color: var(--gray-700);
  font-weight: 500;
}

.course-grade {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding-top: 0.5rem;
  border-top: 1px solid var(--gray-100);
}

.grade-label {
  font-size: 0.8125rem;
  color: var(--gray-500);
}

.grade-value {
  font-size: 1.25rem;
  font-weight: 700;
}

.grade-excellent { color: var(--success); }
.grade-good { color: var(--primary); }
.grade-pass { color: var(--warning); }
.grade-fail { color: var(--danger); }

.course-footer {
  padding-top: 0.5rem;
  border-top: 1px solid var(--gray-100);
  display: flex;
  gap: 0.5rem;
}
</style>
