<script setup>
defineProps({ isGanttView: Boolean })
const emit = defineEmits(['toggleView', 'create', 'openMemo', 'openMemoHistory', 'openLinks'])
</script>

<template>
  <div class="toolbar">
    <div class="toolbar-left">
      <h2 class="toolbar-title">排期助手</h2>
    </div>
    <div class="toolbar-right">
      <button class="btn btn-link" @click="emit('openLinks')">
        🔗 链接收藏
      </button>
      <button class="btn btn-memo" @click="emit('openMemo')">
        📝 当日备忘
      </button>
      <button class="btn btn-memo-history" @click="emit('openMemoHistory')">
        📋 查看备忘
      </button>
      <button class="btn btn-primary" @click="emit('create')">
        + 录入
      </button>
      <button class="btn btn-secondary" @click="emit('export')">
        导出
      </button>
      <button
        class="btn btn-accent"
        @click="emit('toggleView')"
      >
        {{ isGanttView ? '☰ 列表视图' : '⬡ 甘特图' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.toolbar-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}
.toolbar-right {
  display: flex;
  gap: 8px;
}
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}
.btn-primary {
  background: #1677ff;
  color: #fff;
}
.btn-primary:hover {
  background: #4096ff;
}
.btn-secondary {
  background: #fff;
  color: #333;
  border: 1px solid #d9d9d9;
}
.btn-secondary:hover {
  border-color: #1677ff;
  color: #1677ff;
}
.btn-accent {
  background: #fff;
  color: #722ed1;
  border: 1px solid #d9d9d9;
}
.btn-accent:hover {
  border-color: #722ed1;
  background: #f9f0ff;
}
.btn-memo {
  background: #fff;
  color: #52c41a;
  border: 1px solid #52c41a;
}
.btn-memo:hover {
  background: #f6ffed;
  border-color: #73d13d;
  color: #73d13d;
}
.btn-memo-history {
  background: #fff;
  color: #fa8c16;
  border: 1px solid #fa8c16;
}
.btn-memo-history:hover {
  background: #fff7e6;
  border-color: #ffa940;
  color: #ffa940;
}
.btn-link {
  background: #fff;
  color: #eb2f96;
  border: 1px solid #eb2f96;
}
.btn-link:hover {
  background: #fff0f6;
  border-color: #f759ab;
  color: #f759ab;
}
</style>
