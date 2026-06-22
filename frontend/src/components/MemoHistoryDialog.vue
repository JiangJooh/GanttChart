<script setup>
import { ref, onMounted } from 'vue'
import { memoApi } from '../api/index.js'

const emit = defineEmits(['close'])

const memoList = ref([])
const loading = ref(false)
const expandedDate = ref(null)
const selectedMemo = ref(null)

function toggleExpand(memo) {
  if (expandedDate.value === memo.memoDate) {
    expandedDate.value = null
    selectedMemo.value = null
  } else {
    expandedDate.value = memo.memoDate
    try {
      selectedMemo.value = JSON.parse(memo.content)
    } catch {
      selectedMemo.value = []
    }
  }
}

function formatDisplayDate(dateStr) {
  const d = new Date(dateStr + 'T00:00:00')
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  return `${dateStr} 星期${weekDays[d.getDay()]}`
}

async function loadHistory() {
  loading.value = true
  try {
    memoList.value = await memoApi.getHistory()
  } catch (err) {
    alert('加载备忘历史失败: ' + err.message)
  } finally {
    loading.value = false
  }
}

onMounted(loadHistory)
</script>

<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-dialog">
      <div class="modal-header">
        <h3>备忘历史记录</h3>
        <button class="close-btn" @click="emit('close')">&times;</button>
      </div>
      <div class="modal-body">
        <div v-if="loading" class="loading-tip">加载中...</div>
        <div v-else-if="memoList.length === 0" class="empty-tip">暂无备忘记录</div>
        <div v-else class="history-list">
          <div
            v-for="memo in memoList"
            :key="memo.id"
            class="history-item"
          >
            <div class="history-header" @click="toggleExpand(memo)">
              <span class="history-date">{{ formatDisplayDate(memo.memoDate) }}</span>
              <span class="expand-icon">{{ expandedDate === memo.memoDate ? '▼' : '▶' }}</span>
            </div>
            <div v-if="expandedDate === memo.memoDate" class="history-content">
              <div v-if="selectedMemo && selectedMemo.length > 0">
                <div
                  v-for="(item, index) in selectedMemo"
                  :key="index"
                  class="history-item-row"
                  :class="{ done: item.done }"
                >
                  <span class="check-badge">{{ item.done ? '✅' : '⬜' }}</span>
                  <span class="item-text">{{ item.text }}</span>
                </div>
              </div>
              <div v-else class="empty-sub-tip">该日备忘为空</div>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-cancel" @click="emit('close')">关闭</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-dialog {
  background: #fff;
  border-radius: 10px;
  width: 520px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 30px rgba(0,0,0,0.15);
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
}
.modal-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}
.close-btn {
  background: none;
  border: none;
  font-size: 22px;
  color: #999;
  cursor: pointer;
  padding: 0 4px;
  line-height: 1;
}
.close-btn:hover { color: #333; }
.modal-body {
  padding: 0;
  flex: 1;
  overflow-y: auto;
}
.loading-tip, .empty-tip {
  text-align: center;
  color: #bbb;
  font-size: 14px;
  padding: 40px 0;
}
.history-list {
  display: flex;
  flex-direction: column;
}
.history-item {
  border-bottom: 1px solid #f0f0f0;
}
.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  cursor: pointer;
  transition: background 0.2s;
}
.history-header:hover {
  background: #f5f7fa;
}
.history-date {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.expand-icon {
  font-size: 12px;
  color: #999;
}
.history-content {
  padding: 0 20px 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.history-item-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 6px;
  background: #fafafa;
}
.history-item-row.done .item-text {
  text-decoration: line-through;
  color: #999;
}
.check-badge {
  font-size: 14px;
  flex-shrink: 0;
}
.item-text {
  font-size: 14px;
  line-height: 1.5;
  word-break: break-all;
}
.empty-sub-tip {
  text-align: center;
  color: #ccc;
  font-size: 13px;
  padding: 10px 0;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 20px;
  border-top: 1px solid #e8e8e8;
}
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}
.btn-cancel {
  background: #fff;
  color: #333;
  border: 1px solid #d9d9d9;
}
.btn-cancel:hover { border-color: #1677ff; color: #1677ff; }
</style>
