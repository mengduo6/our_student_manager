<template>
  <div class="t-course-card card">
    <div class="t-card-header">
      <div class="t-card-title-row">
        <h3 class="t-course-name">{{ course.name || course.subject }}</h3>
        <span class="badge badge-primary">{{ course.enrollmentCount || 0 }} 人选课</span>
      </div>
      <div class="t-card-actions">
        <button class="btn btn-outline btn-sm" @click="$emit('edit', course)">编辑</button>
        <button class="btn btn-outline btn-sm" @click="$emit('viewStudents', course)">查看学生</button>
        <button class="btn btn-danger btn-sm" @click="$emit('delete', course)">删除</button>
      </div>
    </div>
    <div class="t-card-body">
      <div class="t-meta">
        <span class="t-meta-item">编号: {{ course.id || course.courseId }}</span>
        <span class="t-meta-item" v-if="course.teacherName">教师: {{ course.teacherName }}</span>
      </div>
      <p class="t-desc" v-if="course.about || course.description">{{ course.about || course.description }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  course: { type: Object, required: true }
})

defineEmits(['edit', 'delete', 'viewStudents'])
</script>

<style scoped>
.t-course-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.t-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.t-card-title-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.t-course-name {
  font-size: 1.0625rem;
  font-weight: 600;
  color: var(--gray-900);
  margin: 0;
}

.t-card-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.t-card-body {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.t-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem 1.25rem;
}

.t-meta-item {
  font-size: 0.8125rem;
  color: var(--gray-500);
}

.t-desc {
  font-size: 0.875rem;
  color: var(--gray-600);
  margin: 0;
  line-height: 1.5;
}
</style>
