<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({ task: Object })
const emit = defineEmits(['save', 'close'])

const form = ref({
  name: '',
  projectName: '',
  assignee: '',
  planStartDate: '',
  planEndDate: '',
  status: 'NOT_STARTED',
  priority: 'MEDIUM'
})

onMounted(() => {
  if (props.task) {
    form.value = {
      name: props.task.name || props.task.taskName || '',
      projectName: props.task.projectName || '',
      assignee: props.task.assignee || '',
      planStartDate: props.task.planStartDate || '',
      planEndDate: props.task.planEndDate || '',
      status: props.task.status || 'NOT_STARTED',
      priority: props.task.priority || 'MEDIUM'
    }
  }
})

function handleSave() {
  if (!form.value.name) {
    alert('请输入任务名称')
    return
  }
  const payload = {
    ...form.value,
    id: props.task?.id || undefined
  }
  if (!payload.id) delete payload.id
  emit('save', payload)
}

function handleBackdrop(e) {
  if (e.target === e.currentTarget) emit('close')
}
</script>

<template>
  <div class="dialog-overlay" @click="handleBackdrop">
    <div class="dialog-box">
      <div class="dialog-header">
        <h3>{{ task ? '编辑任务' : '录入任务' }}</h3>
        <button class="close-btn" @click="emit('close')">&times;</button>
      </div>
      <div class="dialog-body">
        <div class="form-item">
          <label>任务名称 *</label>
          <input v-model="form.name" type="text" placeholder="请输入任务名称" />
        </div>
        <div class="form-row">
          <div class="form-item flex-1">
            <label>所属项目</label>
            <input v-model="form.projectName" type="text" placeholder="项目名称" />
          </div>
          <div class="form-item flex-1">
            <label>负责人</label>
            <input v-model="form.assignee" type="text" placeholder="负责人" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-item flex-1">
            <label>计划开始时间</label>
            <input v-model="form.planStartDate" type="date" />
          </div>
          <div class="form-item flex-1">
            <label>计划结束时间</label>
            <input v-model="form.planEndDate" type="date" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-item flex-1">
            <label>状态</label>
            <select v-model="form.status">
              <option value="NOT_STARTED">未开始</option>
              <option value="IN_PROGRESS">进行中</option>
              <option value="COMPLETED">已完成</option>
              <option value="DELAYED">已逾期</option>
            </select>
          </div>
          <div class="form-item flex-1">
            <label>优先级</label>
            <select v-model="form.priority">
              <option value="HIGH">高</option>
              <option value="MEDIUM">中</option>
              <option value="LOW">低</option>
            </select>
          </div>
        </div>
      </div>
      <div class="dialog-footer">
        <button class="btn btn-default" @click="emit('close')">取消</button>
        <button class="btn btn-primary" @click="handleSave">保存</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.dialog-box {
  background: #fff;
  border-radius: 8px;
  width: 520px;
  max-width: 90vw;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.dialog-header h3 {
  font-size: 16px;
  font-weight: 600;
}
.close-btn {
  background: none;
  border: none;
  font-size: 22px;
  color: #999;
  cursor: pointer;
  line-height: 1;
}
.close-btn:hover { color: #333; }
.dialog-body {
  padding: 20px;
}
.form-item {
  margin-bottom: 14px;
}
.form-item label {
  display: block;
  font-size: 13px;
  color: #555;
  margin-bottom: 4px;
  font-weight: 500;
}
.form-item input, .form-item select {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}
.form-item input:focus, .form-item select:focus {
  border-color: #1677ff;
  box-shadow: 0 0 0 2px rgba(22,119,255,0.1);
}
.form-row {
  display: flex;
  gap: 12px;
}
.flex-1 { flex: 1; }
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
}
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
}
.btn-default {
  background: #fff;
  color: #333;
  border: 1px solid #d9d9d9;
}
.btn-default:hover { border-color: #1677ff; color: #1677ff; }
.btn-primary {
  background: #1677ff;
  color: #fff;
}
.btn-primary:hover { background: #4096ff; }
</style>
