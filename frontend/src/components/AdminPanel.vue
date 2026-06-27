<template>
  <div class="admin-panel">

    <!-- ============================ Student Tab ============================ -->
    <div v-if="activeTab === 'students'" class="tab-content">
      <div class="section-header">
        <h2 class="section-title">学生管理</h2>
        <div class="section-actions">
          <button class="btn btn-outline btn-sm" @click="$emit('toggleSelectStudents')">
            {{ selectingStudents ? '取消选择' : '批量选择' }}
          </button>
          <button
            v-if="selectingStudents"
            class="btn btn-danger btn-sm"
            :disabled="selectedStudentIds.length === 0"
            @click="confirmBatchDeleteStudents"
          >批量删除 ({{ selectedStudentIds.length }})</button>
          <button class="btn btn-primary" @click="$emit('openCreateStudent')">+ 新建学生</button>
        </div>
      </div>

      <div v-if="loading.students" class="loading"><span class="spinner"></span></div>
      <div v-else-if="students.length === 0" class="empty-state">
        <div class="empty-state-icon">📋</div>
        <div class="empty-state-text">暂无学生数据</div>
      </div>
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th v-if="selectingStudents" style="width:40px">选</th>
              <th>ID</th>
              <th>姓名</th>
              <th>用户名</th>
              <th>专业</th>
              <th>班级</th>
              <th>邮箱</th>
              <th>电话</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in students" :key="s.id">
              <td v-if="selectingStudents">
                <input type="checkbox" :value="s.id" v-model="selectedStudentIds" />
              </td>
              <td>{{ s.id }}</td>
              <td>{{ s.name }}</td>
              <td>{{ s.username }}</td>
              <td>{{ s.major || '-' }}</td>
              <td>{{ s.className || '-' }}</td>
              <td>{{ s.email || '-' }}</td>
              <td>{{ s.phone || '-' }}</td>
              <td>
                <span class="badge" :class="s.status === 0 ? 'badge-success' : s.status === 1 ? 'badge-warning' : 'badge-danger'">
                  {{ s.statusLabel || (s.status === 0 ? '在读' : s.status === 1 ? '班长' : '休学') }}
                </span>
              </td>
              <td class="action-cell">
                <button class="btn btn-outline btn-sm" @click="$emit('openEditStudent', s)">编辑</button>
                <button v-if="s.status === 0" class="btn btn-warning btn-sm" @click="$emit('updateStudentStatus', s, 2)">休学</button>
                <button v-if="s.status === 2" class="btn btn-success btn-sm" @click="$emit('updateStudentStatus', s, 0)">复学</button>
                <button v-if="s.status !== 1" class="btn btn-outline btn-sm" @click="$emit('appointMonitor', s)">任班长</button>
                <button v-if="s.status === 1" class="btn btn-outline btn-sm" @click="$emit('dismissMonitor', s)">解班长</button>
                <button class="btn btn-danger btn-sm" @click="$emit('deleteStudent', s)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ============================ Teacher Tab ============================ -->
    <div v-if="activeTab === 'teachers'" class="tab-content">
      <div class="section-header">
        <h2 class="section-title">教师管理</h2>
        <div class="section-actions">
          <button class="btn btn-outline btn-sm" @click="$emit('toggleSelectTeachers')">
            {{ selectingTeachers ? '取消选择' : '批量选择' }}
          </button>
          <button
            v-if="selectingTeachers"
            class="btn btn-danger btn-sm"
            :disabled="selectedTeacherIds.length === 0"
            @click="confirmBatchDeleteTeachers"
          >批量删除 ({{ selectedTeacherIds.length }})</button>
          <button class="btn btn-primary" @click="$emit('openCreateTeacher')">+ 新建教师</button>
        </div>
      </div>

      <div v-if="loading.teachers" class="loading"><span class="spinner"></span></div>
      <div v-else-if="teachers.length === 0" class="empty-state">
        <div class="empty-state-icon">👨‍🏫</div>
        <div class="empty-state-text">暂无教师数据</div>
      </div>
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th v-if="selectingTeachers" style="width:40px">选</th>
              <th>ID</th>
              <th>姓名</th>
              <th>用户名</th>
              <th>院系</th>
              <th>职称</th>
              <th>邮箱</th>
              <th>电话</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="t in teachers" :key="t.id">
              <td v-if="selectingTeachers">
                <input type="checkbox" :value="t.id" v-model="selectedTeacherIds" />
              </td>
              <td>{{ t.id }}</td>
              <td>{{ t.name }}</td>
              <td>{{ t.username }}</td>
              <td>{{ t.department || '-' }}</td>
              <td>{{ t.title || '-' }}</td>
              <td>{{ t.email || '-' }}</td>
              <td>{{ t.phone || '-' }}</td>
              <td>
                <span class="badge" :class="t.status === 0 ? 'badge-success' : t.status === 1 ? 'badge-danger' : 'badge-gray'">
                  {{ t.statusLabel || (t.status === 0 ? '在职' : t.status === 1 ? '超级管理员' : '退休') }}
                </span>
              </td>
              <td class="action-cell">
                <button class="btn btn-outline btn-sm" @click="$emit('openEditTeacher', t)">编辑</button>
                <button v-if="t.status === 0" class="btn btn-warning btn-sm" @click="$emit('updateTeacherStatus', t, 2)">退休</button>
                <button v-if="t.status === 2" class="btn btn-success btn-sm" @click="$emit('updateTeacherStatus', t, 0)">恢复</button>
                <button class="btn btn-danger btn-sm" @click="$emit('deleteTeacher', t)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ============================ Class Tab ============================ -->
    <div v-if="activeTab === 'classes'" class="tab-content">
      <div class="section-header">
        <h2 class="section-title">班级管理</h2>
        <div class="section-actions">
          <button class="btn btn-primary" @click="$emit('openCreateClass')">+ 新建班级</button>
        </div>
      </div>
      <div v-if="loading.classes" class="loading"><span class="spinner"></span></div>
      <div v-else-if="classes.length === 0" class="empty-state">
        <div class="empty-state-icon">🏫</div>
        <div class="empty-state-text">暂无班级数据</div>
      </div>
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>班级名称</th>
              <th>班长</th>
              <th>人数</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cls in classes" :key="cls.id">
              <td>{{ cls.id }}</td>
              <td>{{ cls.name }}</td>
              <td>{{ cls.monitorName || '-' }}</td>
              <td>{{ cls.studentCount || 0 }}</td>
              <td>
                <button class="btn btn-outline btn-sm" @click="$emit('openEditClass', cls)">编辑</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ============================ Course Tab ============================ -->
    <div v-if="activeTab === 'courses'" class="tab-content">
      <div class="section-header">
        <h2 class="section-title">课程管理</h2>
      </div>
      <div v-if="loading.courses" class="loading"><span class="spinner"></span></div>
      <div v-else-if="courses.length === 0" class="empty-state">
        <div class="empty-state-icon">📖</div>
        <div class="empty-state-text">暂无课程数据</div>
      </div>
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>课程名称</th>
              <th>授课教师</th>
              <th>选课人数</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="course in courses" :key="course.id">
              <td>{{ course.id }}</td>
              <td>{{ course.name }}</td>
              <td>{{ course.teacherName || '-' }}</td>
              <td>{{ course.enrollmentCount || 0 }}</td>
              <td>
                <button class="btn btn-danger btn-sm" @click="$emit('deleteCourse', course)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  activeTab: { type: String, required: true },
  students: { type: Array, default: () => [] },
  teachers: { type: Array, default: () => [] },
  classes: { type: Array, default: () => [] },
  courses: { type: Array, default: () => [] },
  loading: { type: Object, default: () => ({ students: false, teachers: false, classes: false, courses: false }) },
  selectingStudents: { type: Boolean, default: false },
  selectingTeachers: { type: Boolean, default: false }
})

const emit = defineEmits([
  'openCreateStudent', 'openEditStudent', 'updateStudentStatus', 'appointMonitor', 'dismissMonitor', 'deleteStudent', 'batchDeleteStudents',
  'openCreateTeacher', 'openEditTeacher', 'updateTeacherStatus', 'deleteTeacher', 'batchDeleteTeachers',
  'openCreateClass', 'openEditClass',
  'deleteCourse',
  'toggleSelectStudents', 'toggleSelectTeachers'
])

const selectedStudentIds = ref([])
const selectedTeacherIds = ref([])

// Clear selections when modes toggled
watch(() => props.selectingStudents, (v) => { if (!v) selectedStudentIds.value = [] })
watch(() => props.selectingTeachers, (v) => { if (!v) selectedTeacherIds.value = [] })

function confirmBatchDeleteStudents() {
  if (selectedStudentIds.value.length === 0) return
  if (!confirm(`确定要批量删除 ${selectedStudentIds.value.length} 名学生吗？\n此操作不可撤销！`)) return
  emit('batchDeleteStudents', [...selectedStudentIds.value])
}

function confirmBatchDeleteTeachers() {
  if (selectedTeacherIds.value.length === 0) return
  if (!confirm(`确定要批量删除 ${selectedTeacherIds.value.length} 名教师吗？\n此操作不可撤销！`)) return
  emit('batchDeleteTeachers', [...selectedTeacherIds.value])
}
</script>

<style scoped>
.admin-panel { width: 100%; }
.tab-content { animation: fadeIn 0.2s ease; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.section-title { margin: 0; font-size: 1.25rem; color: #1a1a2e; }
.section-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-wrap: wrap;
}
.action-cell {
  display: flex;
  gap: 0.3rem;
  flex-wrap: wrap;
}
</style>
