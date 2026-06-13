<script setup>
import { ref, computed, onMounted } from 'vue'
import { ganttApi } from '../api/index.js'

const emit = defineEmits(['edit', 'create'])

const ganttData = ref(null)
const loading = ref(true)
const error = ref('')
const tooltip = ref({ show: false, x: 0, y: 0, task: null })
const scrollContainer = ref(null)

const DAY_WIDTH = 36
const ROW_HEIGHT = 40
const LABEL_WIDTH = 220

// Generate all dates between start and end
function getDateRange(start, end) {
  const dates = []
  const d = new Date(start)
  while (d <= end) {
    dates.push(new Date(d))
    d.setDate(d.getDate() + 1)
  }
  return dates
}

function isWeekend(dateStr) {
  const d = new Date(dateStr)
  const day = d.getDay()
  return day === 0 || day === 6
}

function formatDate(d) {
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${m}-${day}`
}

function formatISO(d) {
  return d.toISOString().slice(0, 10)
}

function getMonthLabel(d) {
  return `${d.getFullYear()}/${String(d.getMonth() + 1).padStart(2, '0')}`
}

const dateRange = computed(() => {
  if (!ganttData.value) return []
  const holidays = ganttData.value.holidays || []
  let earliest = null, latest = null

  for (const p of ganttData.value.projects) {
    for (const t of p.tasks) {
      if (t.planStartDate) {
        const d = new Date(t.planStartDate)
        if (!earliest || d < earliest) earliest = d
      }
      if (t.planEndDate) {
        const d = new Date(t.planEndDate)
        if (!latest || d > latest) latest = d
      }
    }
  }

  if (!earliest || !latest) return []

  const rangeStart = new Date(earliest)
  rangeStart.setDate(rangeStart.getDate() - 3)
  const rangeEnd = new Date(latest)
  rangeEnd.setDate(rangeEnd.getDate() + 7)

  return getDateRange(rangeStart, rangeEnd).map(d => {
    const ds = formatISO(d)
    return {
      date: ds,
      label: formatDate(d),
      dayOfWeek: d.getDay(),
      weekend: isWeekend(ds),
      holiday: holidays.includes(ds),
      monthLabel: d.getDate() === 1 || d.getDate() === rangeStart.getDate()
        ? getMonthLabel(d) : null
    }
  })
})

const totalWidth = computed(() => dateRange.value.length * DAY_WIDTH)

const taskRows = computed(() => {
  if (!ganttData.value) return []
  const rows = []
  const range = dateRange.value
  if (range.length === 0) return []

  const rangeStart = new Date(range[0].date)
  const rangeEnd = new Date(range[range.length - 1].date)
  const totalDays = (rangeEnd - rangeStart) / (1000 * 60 * 60 * 24) || 1

  function getLeft(startDate) {
    const d = new Date(startDate)
    const diff = (d - rangeStart) / (1000 * 60 * 60 * 24)
    return (diff / totalDays) * 100
  }

  function getWidth(startDate, endDate) {
    const s = new Date(startDate)
    const e = new Date(endDate)
    const diff = (e - s) / (1000 * 60 * 60 * 24) + 1
    return (diff / totalDays) * 100
  }

  for (const project of ganttData.value.projects) {
    rows.push({ type: 'group', projectName: project.projectName })
    for (const task of project.tasks) {
      const barStyle = {
        left: getLeft(task.planStartDate) + '%',
        width: getWidth(task.planStartDate, task.planEndDate) + '%'
      }
      const isDelayed = task.status !== 'COMPLETED' &&
        new Date(task.planEndDate) < new Date()

      let barClass = 'bar-not-started'
      if (task.status === 'IN_PROGRESS') barClass = 'bar-in-progress'
      else if (task.status === 'DELAYED' || isDelayed) barClass = 'bar-delayed'

      rows.push({
        type: 'task',
        task,
        barStyle,
        barClass,
        barLabel: task.taskName
      })
    }
  }
  return rows
})

const todayLine = computed(() => {
  const range = dateRange.value
  if (range.length === 0) return null
  const today = new Date()
  const rangeStart = new Date(range[0].date)
  const rangeEnd = new Date(range[range.length - 1].date)
  const totalDays = (rangeEnd - rangeStart) / (1000 * 60 * 60 * 24) || 1
  const diff = (today - rangeStart) / (1000 * 60 * 60 * 24)
  if (diff < 0 || diff > totalDays) return null
  return { left: (diff / totalDays) * 100 + '%' }
})

function onMouseEnter(e, task) {
  const rect = e.target.getBoundingClientRect()
  tooltip.value = {
    show: true,
    x: rect.left + rect.width / 2,
    y: rect.top - 8,
    task
  }
}

function onMouseLeave() {
  tooltip.value.show = false
}

function onTaskClick(task) {
  emit('edit', {
    id: task.taskId,
    taskName: task.taskName,
    assignee: task.assignee,
    planStartDate: task.planStartDate,
    planEndDate: task.planEndDate,
    status: task.status,
    priority: task.priority
  })
}

const statusMap = {
  NOT_STARTED: '未开始',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  DELAYED: '已逾期'
}

onMounted(() => {
  loading.value = true
  error.value = ''
  ganttApi.getGanttData()
    .then(data => { ganttData.value = data })
    .catch(err => { error.value = err.message })
    .finally(() => { loading.value = false })
})
</script>

<template>
  <div class="gantt-wrapper">
    <!-- Loading -->
    <div v-if="loading" class="gantt-loading">
      <div class="spinner"></div>
      <p>加载甘特图数据...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="gantt-error">
      <p>加载失败: {{ error }}</p>
      <button class="btn btn-primary" @click="$emit('create')">重新加载</button>
    </div>

    <!-- Empty -->
    <div v-else-if="!ganttData || ganttData.projects.length === 0" class="gantt-empty">
      <p>暂无排期中未上线的任务</p>
      <button class="btn btn-primary" @click="$emit('create')">录入任务</button>
    </div>

    <!-- Gantt Chart -->
    <div v-else class="gantt-layout">
      <!-- Legend -->
      <div class="gantt-legend">
        <span class="legend-item"><span class="legend-dot weekend"></span> 周末</span>
        <span class="legend-item"><span class="legend-dot holiday"></span> 节假日</span>
        <span class="legend-item"><span class="legend-dot" style="background:#5B9BD5"></span> 进行中</span>
        <span class="legend-item"><span class="legend-dot" style="background:#A6A6A6"></span> 未开始</span>
        <span class="legend-item"><span class="legend-dot" style="background:#FF6B6B"></span> 逾期</span>
      </div>

      <div class="gantt-body">
        <!-- Left label column -->
        <div class="gantt-labels" :style="{ width: LABEL_WIDTH + 'px' }">
          <div class="label-header">任务名称</div>
          <div
            v-for="(row, i) in taskRows"
            :key="i"
            :class="['label-row', row.type === 'group' ? 'label-group' : '']"
            :style="{ height: ROW_HEIGHT + 'px' }"
          >
            <span v-if="row.type === 'group'">{{ row.projectName }}</span>
            <span v-else class="task-name-text">{{ row.task.taskName }}</span>
          </div>
        </div>

        <!-- Gantt area with scroll -->
        <div class="gantt-scroll" ref="scrollContainer">
          <div class="gantt-chart" :style="{ width: totalWidth + 'px' }">
            <!-- Date header -->
            <div class="gantt-header">
              <div
                v-for="(d, i) in dateRange"
                :key="i"
                :class="[
                  'date-cell',
                  d.weekend ? 'cell-weekend' : '',
                  d.holiday ? 'cell-holiday' : '',
                  d.dayOfWeek === 0 ? 'cell-sun' : ''
                ]"
                :style="{ width: DAY_WIDTH + 'px' }"
              >
                <div v-if="d.monthLabel" class="month-label">{{ d.monthLabel }}</div>
                <div class="day-label">{{ d.label }}</div>
              </div>
            </div>

            <!-- Rows -->
            <div class="gantt-rows">
              <template v-for="(row, ri) in taskRows" :key="ri">
                <!-- Group row -->
                <div
                  v-if="row.type === 'group'"
                  class="gantt-row gantt-group-row"
                  :style="{ height: ROW_HEIGHT + 'px' }"
                >
                  <div
                    v-for="(d, di) in dateRange"
                    :key="di"
                    :class="['row-cell', d.weekend ? 'cell-weekend' : '', d.holiday ? 'cell-holiday' : '']"
                    :style="{ width: DAY_WIDTH + 'px' }"
                  ></div>
                </div>

                <!-- Task row -->
                <div
                  v-else
                  class="gantt-row"
                  :style="{ height: ROW_HEIGHT + 'px' }"
                >
                  <div class="row-bg" :style="{ position: 'relative', height: '100%' }">
                    <div
                      v-for="(d, di) in dateRange"
                      :key="di"
                      :class="['row-cell', d.weekend ? 'cell-weekend' : '', d.holiday ? 'cell-holiday' : '']"
                      :style="{ width: DAY_WIDTH + 'px' }"
                    ></div>
                    <!-- Task bar -->
                    <div
                      :class="['task-bar', row.barClass]"
                      :style="row.barStyle"
                      @mouseenter="(e) => onMouseEnter(e, row.task)"
                      @mouseleave="onMouseLeave"
                      @click="onTaskClick(row.task)"
                    >
                      <span class="bar-text">{{ row.barLabel }}</span>
                    </div>
                    <!-- Today line -->
                    <div
                      v-if="todayLine && ri === 0"
                      class="today-line"
                      :style="{ left: todayLine.left }"
                    ></div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- Tooltip -->
      <div
        v-if="tooltip.show"
        class="gantt-tooltip"
        :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }"
      >
        <div class="tip-title">{{ tooltip.task.taskName }}</div>
        <div class="tip-row">负责人: {{ tooltip.task.assignee || '-' }}</div>
        <div class="tip-row">计划: {{ tooltip.task.planStartDate }} ~ {{ tooltip.task.planEndDate }}</div>
        <div class="tip-row">状态: {{ statusMap[tooltip.task.status] || tooltip.task.status }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.gantt-wrapper {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  position: relative;
  overflow: hidden;
}
.gantt-loading, .gantt-error, .gantt-empty {
  text-align: center;
  padding: 80px 0;
  color: #999;
}
.spinner {
  width: 32px; height: 32px;
  border: 3px solid #eee;
  border-top-color: #1677ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.gantt-legend {
  display: flex;
  gap: 16px;
  padding: 10px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 12px;
  color: #666;
}
.legend-dot {
  display: inline-block;
  width: 10px; height: 10px;
  border-radius: 2px;
  margin-right: 4px;
  vertical-align: middle;
}
.legend-dot.weekend { background: #f0f0f0; }
.legend-dot.holiday { background: #fde8e8; }
.gantt-body {
  display: flex;
  overflow: hidden;
}
.gantt-labels {
  flex-shrink: 0;
  border-right: 1px solid #e8e8e8;
  overflow: hidden;
}
.label-header {
  height: 44px;
  line-height: 44px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 600;
  color: #555;
  border-bottom: 1px solid #e8e8e8;
  background: #fafafa;
}
.label-row {
  display: flex;
  align-items: center;
  padding: 0 12px;
  font-size: 13px;
  border-bottom: 1px solid #f5f5f5;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.label-group {
  background: #f9f9f9;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}
.task-name-text {
  color: #555;
  font-size: 12px;
}
.gantt-scroll {
  overflow-x: auto;
  overflow-y: hidden;
  flex: 1;
}
.gantt-chart {
  position: relative;
  min-height: 100%;
}
.gantt-header {
  display: flex;
  height: 44px;
  border-bottom: 1px solid #e8e8e8;
  background: #fafafa;
}
.date-cell {
  flex-shrink: 0;
  text-align: center;
  border-right: 1px solid #f0f0f0;
  font-size: 11px;
  color: #888;
}
.month-label {
  height: 18px;
  line-height: 18px;
  font-size: 11px;
  font-weight: 600;
  color: #555;
}
.day-label {
  height: 26px;
  line-height: 26px;
}
.cell-weekend {
  background: #f0f0f0;
}
.cell-holiday {
  background: #fde8e8;
}
.cell-sun {
  color: #d4380d;
}
.gantt-rows {
  position: relative;
}
.gantt-row {
  display: flex;
  border-bottom: 1px solid #f5f5f5;
  position: relative;
}
.gantt-group-row {
  background: #f9f9f9;
}
.row-bg {
  display: flex;
  flex: 1;
  position: relative;
}
.row-cell {
  flex-shrink: 0;
  border-right: 1px solid #f5f5f5;
}
.task-bar {
  position: absolute;
  top: 6px;
  height: 28px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0 8px;
  min-width: 4px;
  overflow: hidden;
  z-index: 2;
  transition: opacity 0.15s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}
.task-bar:hover {
  opacity: 0.85;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}
.bar-not-started {
  background: #A6A6A6;
  color: #fff;
}
.bar-in-progress {
  background: #5B9BD5;
  color: #fff;
}
.bar-delayed {
  background: #FF6B6B;
  color: #fff;
}
.bar-text {
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.today-line {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #ff4d4f;
  z-index: 3;
  pointer-events: none;
}
.today-line::before {
  content: '';
  position: absolute;
  top: -4px;
  left: -3px;
  width: 8px;
  height: 8px;
  background: #ff4d4f;
  border-radius: 50%;
}
.gantt-tooltip {
  position: fixed;
  transform: translate(-50%, -100%);
  background: #333;
  color: #fff;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  line-height: 1.6;
  z-index: 1000;
  pointer-events: none;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  max-width: 260px;
}
.tip-title {
  font-weight: 600;
  margin-bottom: 4px;
}
.tip-row {
  opacity: 0.9;
}
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
}
.btn-primary {
  background: #1677ff;
  color: #fff;
}
.btn-primary:hover {
  background: #4096ff;
}
</style>
