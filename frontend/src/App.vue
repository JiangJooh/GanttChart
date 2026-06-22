<script setup>
import { ref, onMounted } from 'vue'
import Toolbar from './components/Toolbar.vue'
import TaskListView from './components/TaskListView.vue'
import GanttChart from './components/GanttChart.vue'
import TaskDialog from './components/TaskDialog.vue'
import MemoDialog from './components/MemoDialog.vue'
import MemoHistoryDialog from './components/MemoHistoryDialog.vue'
import LinkDialog from './components/LinkDialog.vue'
import { taskApi } from './api/index.js'

const isGanttView = ref(false)
const taskList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingTask = ref(null)
const ganttKey = ref(0)
const memoDialogVisible = ref(false)
const memoHistoryVisible = ref(false)
const linkDialogVisible = ref(false)

function loadTasks() {
  loading.value = true
  taskApi.list()
    .then(data => { taskList.value = data })
    .catch(err => alert('加载任务失败: ' + err.message))
    .finally(() => { loading.value = false })
}

function toggleView() {
  isGanttView.value = !isGanttView.value
}

function handleExport() {
  alert('导出功能开发中')
}

function openCreate() {
  editingTask.value = null
  dialogVisible.value = true
}

function openEdit(task) {
  editingTask.value = { ...task }
  dialogVisible.value = true
}

function handleSave(task) {
  const promise = task.id
    ? taskApi.update(task.id, task)
    : taskApi.create(task)
  promise
    .then(() => {
      dialogVisible.value = false
      loadTasks()
      if (isGanttView.value) ganttKey.value++
    })
    .catch(err => alert('保存失败: ' + err.message))
}

function handleDelete(id) {
  if (!confirm('确定删除该任务吗？')) return
  taskApi.delete(id)
    .then(() => loadTasks())
    .catch(err => alert('删除失败: ' + err.message))
}

function handleSetLaunch(id, date) {
  taskApi.setLaunchDate(id, date)
    .then(() => loadTasks())
    .catch(err => alert('设置上线时间失败: ' + err.message))
}

function openMemo() {
  memoDialogVisible.value = true
}

function openMemoHistory() {
  memoHistoryVisible.value = true
}

function openLinks() {
  linkDialogVisible.value = true
}

onMounted(loadTasks)
</script>

<template>
  <div class="app-container">
    <Toolbar
      :isGanttView="isGanttView"
      @toggleView="toggleView"
      @create="openCreate"
      @export="handleExport"
      @openMemo="openMemo"
      @openMemoHistory="openMemoHistory"
      @openLinks="openLinks"
    />
    <div class="view-container">
      <TaskListView
        v-if="!isGanttView"
        :tasks="taskList"
        :loading="loading"
        @edit="openEdit"
        @delete="handleDelete"
        @launch="handleSetLaunch"
        @create="openCreate"
      />
      <GanttChart
        v-else
        :key="ganttKey"
        @edit="openEdit"
        @create="openCreate"
      />
    </div>
    <TaskDialog
      v-if="dialogVisible"
      :task="editingTask"
      @save="handleSave"
      @close="dialogVisible = false"
    />
    <MemoDialog
      v-if="memoDialogVisible"
      @close="memoDialogVisible = false"
    />
    <MemoHistoryDialog
      v-if="memoHistoryVisible"
      @close="memoHistoryVisible = false"
    />
    <LinkDialog
      v-if="linkDialogVisible"
      @close="linkDialogVisible = false"
    />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  background: #f5f7fa;
  color: #333;
}
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.view-container {
  flex: 1;
  padding: 16px 20px;
}
</style>
