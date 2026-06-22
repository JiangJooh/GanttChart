<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { memoApi } from '../api/index.js'

const emit = defineEmits(['close'])

const today = new Date()
const dateStr = ref(formatDate(today))
const items = ref([])
const newItemText = ref('')
const saving = ref(false)
const inputRef = ref(null)

function formatDate(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function addItem() {
  const text = newItemText.value.trim()
  if (!text) return
  items.value.push({ text, done: false })
  newItemText.value = ''
  nextTick(() => {
    inputRef.value?.focus()
  })
}

function toggleItem(index) {
  items.value[index].done = !items.value[index].done
}

function removeItem(index) {
  items.value.splice(index, 1)
}

function handleKeydown(e) {
  if (e.key === 'Enter') {
    addItem()
  }
}

async function loadTodayMemo() {
  try {
    const data = await memoApi.getToday(dateStr.value)
    if (data && data.content) {
      try {
        items.value = JSON.parse(data.content)
      } catch {
        items.value = []
      }
    }
  } catch {
    // no memo for today, start fresh
  }
}

async function handleSave() {
  saving.value = true
  try {
    await memoApi.save({
      memoDate: dateStr.value,
      content: JSON.stringify(items.value)
    })
    emit('close')
  } catch (err) {
    alert('保存失败: ' + err.message)
  } finally {
    saving.value = false
  }
}

onMounted(loadTodayMemo)
</script>

<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-dialog">
      <div class="modal-header">
        <h3>当日备忘 - {{ dateStr }}</h3>
        <button class="close-btn" @click="emit('close')">&times;</button>
      </div>
      <div class="modal-body">
        <div class="input-row">
          <input
            ref="inputRef"
            v-model="newItemText"
            class="memo-input"
            placeholder="输入今日事项，按回车添加"
            @keydown="handleKeydown"
          />
          <button class="btn btn-add" @click="addItem">添加</button>
        </div>
        <div class="item-list" v-if="items.length > 0">
          <div
            v-for="(item, index) in items"
            :key="index"
            class="memo-item"
            :class="{ done: item.done }"
          >
            <label class="checkbox-label">
              <input type="checkbox" :checked="item.done" @change="toggleItem(index)" />
              <span class="checkmark"></span>
            </label>
            <span class="item-text" @dblclick="toggleItem(index)">{{ item.text }}</span>
            <button class="delete-item-btn" @click="removeItem(index)" title="删除">×</button>
          </div>
        </div>
        <div class="empty-tip" v-else>暂无事项，请在上方输入添加</div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-cancel" @click="emit('close')">取消</button>
        <button class="btn btn-primary" :disabled="saving" @click="handleSave">
          {{ saving ? '保存中...' : '保存' }}
        </button>
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
  padding: 16px 20px;
  flex: 1;
  overflow-y: auto;
}
.input-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.memo-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}
.memo-input:focus {
  border-color: #1677ff;
  box-shadow: 0 0 0 2px rgba(22,119,255,0.1);
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
.btn-add {
  background: #1677ff;
  color: #fff;
  white-space: nowrap;
}
.btn-add:hover { background: #4096ff; }
.btn-primary {
  background: #1677ff;
  color: #fff;
}
.btn-primary:hover { background: #4096ff; }
.btn-primary:disabled {
  background: #a0c4ff;
  cursor: not-allowed;
}
.btn-cancel {
  background: #fff;
  color: #333;
  border: 1px solid #d9d9d9;
}
.btn-cancel:hover { border-color: #1677ff; color: #1677ff; }
.item-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.memo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 6px;
  background: #fafafa;
  transition: background 0.2s;
}
.memo-item:hover {
  background: #f0f5ff;
}
.memo-item.done .item-text {
  text-decoration: line-through;
  color: #999;
}
.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #1677ff;
}
.item-text {
  flex: 1;
  font-size: 14px;
  line-height: 1.5;
  cursor: default;
  word-break: break-all;
}
.delete-item-btn {
  background: none;
  border: none;
  color: #ccc;
  font-size: 18px;
  cursor: pointer;
  padding: 0 2px;
  line-height: 1;
  visibility: hidden;
}
.memo-item:hover .delete-item-btn {
  visibility: visible;
}
.delete-item-btn:hover { color: #ff4d4f; }
.empty-tip {
  text-align: center;
  color: #bbb;
  font-size: 14px;
  padding: 30px 0;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 20px;
  border-top: 1px solid #e8e8e8;
}
</style>
