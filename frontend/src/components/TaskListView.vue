<script setup>
import { computed } from 'vue'

const props = defineProps({ tasks: Array, loading: Boolean })
const emit = defineEmits(['edit', 'delete', 'launch'])

const statusMap = {
  NOT_STARTED: { label: '未开始', class: 'tag-default' },
  IN_PROGRESS: { label: '进行中', class: 'tag-processing' },
  COMPLETED: { label: '已完成', class: 'tag-success' },
  DELAYED: { label: '已逾期', class: 'tag-error' }
}

function statusInfo(s) {
  return statusMap[s] || { label: s, class: 'tag-default' }
}
</script>

<template>
  <div class="list-container">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    <div v-else-if="tasks.length === 0" class="empty-state">
      <p>暂无任务数据</p>
      <button class="btn btn-primary" @click="emit('create')">录入任务</button>
    </div>
    <table v-else class="task-table">
      <thead>
        <tr>
          <th>任务名称</th>
          <th>所属项目</th>
          <th>负责人</th>
          <th>计划开始</th>
          <th>计划结束</th>
          <th>实际上线</th>
          <th>状态</th>
          <th>优先级</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="t in tasks" :key="t.id">
          <td>{{ t.name }}</td>
          <td>{{ t.projectName }}</td>
          <td>{{ t.assignee }}</td>
          <td>{{ t.planStartDate }}</td>
          <td>{{ t.planEndDate }}</td>
          <td>{{ t.actualLaunchDate || '-' }}</td>
          <td>
            <span :class="['tag', statusInfo(t.status).class]">
              {{ statusInfo(t.status).label }}
            </span>
          </td>
          <td>{{ t.priority }}</td>
          <td class="actions">
            <button class="link-btn" @click="emit('edit', t)">编辑</button>
            <button
              v-if="t.status !== 'COMPLETED'"
              class="link-btn"
              @click="emit('launch', t.id, new Date().toISOString().slice(0,10))"
            >
              设为上线
            </button>
            <button class="link-btn danger" @click="emit('delete', t.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.list-container {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.loading-state, .empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #eee;
  border-top-color: #1677ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.task-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.task-table th {
  text-align: left;
  padding: 10px 12px;
  background: #fafafa;
  border-bottom: 2px solid #e8e8e8;
  font-weight: 600;
  color: #555;
  white-space: nowrap;
}
.task-table td {
  padding: 10px 12px;
  border-bottom: 1px solid #f0f0f0;
}
.task-table tr:hover td {
  background: #fafbff;
}
.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.tag-default { background: #f5f5f5; color: #666; }
.tag-processing { background: #e6f4ff; color: #1677ff; }
.tag-success { background: #f6ffed; color: #52c41a; }
.tag-error { background: #fff2f0; color: #ff4d4f; }
.actions { white-space: nowrap; }
.link-btn {
  background: none;
  border: none;
  color: #1677ff;
  cursor: pointer;
  font-size: 13px;
  margin-right: 8px;
  padding: 2px 4px;
}
.link-btn:hover { color: #4096ff; }
.link-btn.danger { color: #ff4d4f; }
.link-btn.danger:hover { color: #ff7875; }
</style>
