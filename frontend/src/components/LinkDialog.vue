<script setup>
import { ref, onMounted } from 'vue'
import { linkApi } from '../api/index.js'

const emit = defineEmits(['close'])

const links = ref([])
const loading = ref(false)
const name = ref('')
const url = ref('')
const priority = ref(0)
const editingId = ref(null)
const saving = ref(false)

function loadLinks() {
  loading.value = true
  linkApi.list()
    .then(data => { links.value = data })
    .catch(err => alert('加载链接失败: ' + err.message))
    .finally(() => { loading.value = false })
}

function resetForm() {
  name.value = ''
  url.value = ''
  priority.value = 0
  editingId.value = null
}

function handleSave() {
  if (!name.value.trim()) {
    alert('请输入名称')
    return
  }
  if (!url.value.trim()) {
    alert('请输入链接')
    return
  }
  const payload = {
    name: name.value.trim(),
    url: url.value.trim(),
    priority: parseInt(priority.value) || 0
  }
  if (editingId.value) {
    payload.id = editingId.value
  }
  saving.value = true
  linkApi.save(payload)
    .then(() => {
      resetForm()
      loadLinks()
    })
    .catch(err => alert('保存失败: ' + err.message))
    .finally(() => { saving.value = false })
}

function handleEdit(item) {
  editingId.value = item.id
  name.value = item.name
  url.value = item.url
  priority.value = item.priority
}

function handleDelete(id) {
  if (!confirm('确定删除该链接吗？')) return
  linkApi.delete(id)
    .then(() => {
      if (editingId.value === id) resetForm()
      loadLinks()
    })
    .catch(err => alert('删除失败: ' + err.message))
}

function openLink(item) {
  window.open(item.url, '_blank')
}

onMounted(loadLinks)
</script>

<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-dialog">
      <div class="modal-header">
        <h3>🔗 链接收藏</h3>
      </div>
      <div class="modal-body">
        <!-- 表单输入区 -->
        <div class="form-section">
          <div class="form-row">
            <div class="form-field">
              <label>名称</label>
              <input v-model="name" type="text" placeholder="输入链接名称" class="input" />
            </div>
            <div class="form-field">
              <label>链接</label>
              <input v-model="url" type="url" placeholder="输入链接地址" class="input" />
            </div>
            <div class="form-field form-field-small">
              <label>优先级</label>
              <input v-model.number="priority" type="number" placeholder="0" class="input" />
            </div>
            <div class="form-action">
              <button class="btn btn-save" :disabled="saving" @click="handleSave">
                {{ saving ? '保存中...' : (editingId ? '更新' : '添加') }}
              </button>
            </div>
          </div>
        </div>

        <!-- 链接列表 -->
        <div class="list-section" v-if="!loading && links.length > 0">
          <div class="list-item" v-for="item in links" :key="item.id">
            <div class="item-left">
              <span class="item-priority">{{ item.priority }}</span>
              <div class="item-info">
                <span class="item-name">{{ item.name }}</span>
                <span class="item-url" :title="item.url">{{ item.url }}</span>
              </div>
            </div>
            <div class="item-actions">
              <button class="btn btn-sm btn-open" @click="openLink(item)" title="打开链接">🔗</button>
              <button class="btn btn-sm btn-edit" @click="handleEdit(item)" title="编辑">✏️</button>
              <button class="btn btn-sm btn-delete" @click="handleDelete(item.id)" title="删除">🗑️</button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty-state" v-if="!loading && links.length === 0">
          <p>暂无收藏链接，在上方添加吧</p>
        </div>

        <!-- 加载状态 -->
        <div class="loading-state" v-if="loading">
          <p>加载中...</p>
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
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-dialog {
  background: #fff;
  border-radius: 8px;
  width: 680px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 6px 30px rgba(0,0,0,0.15);
}
.modal-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}
.modal-header h3 {
  margin: 0;
  font-size: 16px;
  color: #1a1a1a;
}
.modal-body {
  padding: 16px 24px;
  flex: 1;
  overflow-y: auto;
}
.modal-footer {
  padding: 12px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}
.form-section {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.form-row {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}
.form-field {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.form-field label {
  font-size: 12px;
  color: #666;
}
.form-field-small {
  flex: 0 0 80px;
}
.form-action {
  flex: 0 0 auto;
}
.input {
  width: 100%;
  padding: 6px 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 13px;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.input:focus {
  border-color: #1677ff;
  box-shadow: 0 0 0 2px rgba(22,119,255,0.1);
}
.btn {
  padding: 6px 14px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn-save {
  background: #1677ff;
  color: #fff;
  white-space: nowrap;
}
.btn-save:hover:not(:disabled) {
  background: #4096ff;
}
.btn-cancel {
  background: #fff;
  color: #333;
  border: 1px solid #d9d9d9;
}
.btn-cancel:hover {
  border-color: #1677ff;
  color: #1677ff;
}
.btn-sm {
  padding: 4px 8px;
  font-size: 14px;
  background: transparent;
}
.btn-open:hover {
  background: #f0f5ff;
}
.btn-edit:hover {
  background: #fff7e6;
}
.btn-delete:hover {
  background: #fff1f0;
}
.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 6px;
  transition: background 0.2s;
}
.list-item:hover {
  background: #fafafa;
}
.list-item + .list-item {
  border-top: 1px solid #f5f5f5;
}
.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}
.item-priority {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  border-radius: 4px;
  background: #f0f5ff;
  color: #1677ff;
  font-size: 12px;
  font-weight: 600;
  padding: 0 6px;
}
.item-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
}
.item-url {
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
  margin-left: 8px;
}
.empty-state, .loading-state {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 14px;
}
</style>
