# 排期助手 - 甘特图

任务排期管理与甘特图可视化工具，支持任务的录入、编辑、删除，以及甘特图视图直观展示任务时间线。

---

## 目录

- [项目架构](#项目架构)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [快速启动](#快速启动)
- [开机自启](#开机自启)
- [项目结构](#项目结构)
- [API 接口](#api-接口)
- [常见问题](#常见问题)

---

## 项目架构

```
┌─────────────┐     ┌─────────────┐     ┌──────────┐
│  Vue 3 前端  │────▶│ Spring Boot  │────▶│  MySQL    │
│  :5173      │     │  后端 :8080  │     │  :3306    │
└─────────────┘     └─────────────┘     └──────────┘
                           │
                    ┌──────┴──────┐
                    │ Caffeine    │
                    │ 缓存(节假日) │
                    └─────────────┘
```

- **前端** (Vue 3 + Vite)：列表视图与甘特图视图切换，通过 Vite proxy 代理 API 请求到后端
- **后端** (Spring Boot + MyBatis)：RESTful API，Caffeine 缓存节假日数据
- **数据库** (MySQL 8.0)：任务表 + 节假日配置表

---

## 功能特性

### 列表视图
- 表格展示所有任务，支持按项目、关键词筛选
- 支持任务的创建、编辑、删除
- "设为上线"一键标记任务完成并记录实际上线时间
- 状态标签着色（未开始 / 进行中 / 已完成 / 已逾期）
- 加载中、空数据、错误状态全覆盖

### 甘特图视图
- **时间线展示**：横轴为日期，纵轴为按项目分组的任务行
- **任务条着色**：
  - 🟦 蓝色 — 进行中
  - ⬜ 灰色 — 未开始
  - 🟥 红色 — 已逾期（当前时间超过计划结束时间）
- **周末/节假日标注**：
  - 浅灰背景 — 周末
  - 浅红背景 — 法定节假日（从数据库读取）
- **今日线**：当前日期红色竖线标记
- **鼠标悬停**：显示任务完整信息的 Tooltip
- **点击任务条**：打开编辑弹窗
- **水平滚动**：支持横向滚动查看完整时间线
- **加载/空/错误状态**：Loading 动画、空状态指引、错误重试

### 视图切换
- 右上角一键切换「列表视图 ⇄ 甘特图视图」
- 在甘特图中编辑任务后，视图自动刷新

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 前端框架 | Vue 3 (Composition API + `<script setup>`) | 3.5+ |
| 构建工具 | Vite | 8.0+ |
| 后端框架 | Spring Boot | 3.4.4 |
| ORM | MyBatis | 3.0.4 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Caffeine | - |
| JDK | Corretto | 21 |
| Node.js | - | 20+ |

---

## 快速启动

### 前置条件

- JDK 21+
- Node.js 20+
- MySQL 8.0+
- Maven 3.6+

### 1. 初始化数据库

```bash
# 方式一：使用命令行导入
mysql -u root -p < sql/init.sql

# 方式二：登录 MySQL 后 source
mysql -u root -p
mysql> source D:/Vibe-Coding/GanttChart/sql/init.sql
```

`init.sql` 会自动创建 `gantt_chart` 库、建表、导入 2026 年节假日数据和样例任务数据。

### 2. 配置数据库连接

编辑 `backend/src/main/resources/application.yml`，修改数据库密码：

```yaml
spring:
  datasource:
    username: root
    password: 你的密码
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动在 `http://localhost:8080`，验证：

```bash
curl http://localhost:8080/api/tasks
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动在 `http://localhost:5173`，打开浏览器访问即可。

> 前端 Vite 已配置 proxy，`/api` 请求自动代理到后端 `8080` 端口，无需额外配置跨域。

---

## 开机自启

项目已配置开机自启，电脑启动后会自动运行 MySQL → 后端 → 前端，无需手动操作。

### 前置条件（只需做一次）

开始自启前，请确保：

1. **MySQL 已设为开机自启**（通常安装时默认已开启）
   ```bash
   # 验证 MySQL 是否为自动启动
   sc qc MySQL80 | findstr AUTO_START
   ```
2. **前后端依赖已安装**
   ```bash
   cd frontend && npm install   # 前端依赖
   cd backend && mvn dependency:resolve   # 后端依赖
   ```

### 启用开机自启

项目目录下的 `startup.bat` 已注册到 Windows 开机启动项中。

启动逻辑：
1. MySQL 服务（系统服务，自动启动）
2. `startup.bat` → `mvn spring-boot:run`（后端，新窗口）
3. 等待后端就绪后 → `npm run dev`（前端，新窗口）

启动完成后访问 http://localhost:5173 即可使用。

### 手动启动 / 重启

如果不希望开机自启，也可以随时手动启动：

```bash
# 直接双击项目目录下的 startup.bat
# 或在命令行中运行：
D:\Vibe-Coding\GanttChart\startup.bat
```

### 取消开机自启

**方法一：运行取消脚本（推荐）**

双击项目目录下的 `remove-startup.bat`，一键移除。

**方法二：手动删除**

1. 按 `Win + R`，输入 `shell:startup`，回车
2. 删除 `GanttChart.lnk` 文件

**方法三：禁止 MySQL 开机自启（不推荐，如需完全关闭）**

```bash
# 将 MySQL 从自动改为手动
sc config MySQL80 start= demand
```

---

## 项目结构

```
GanttChart/
├── PRD_甘特图功能.md          # 产品需求文档
├── startup.bat                # 一键启动脚本（已注册开机自启）
├── remove-startup.bat         # 取消开机自启脚本
├── sql/
│   └── init.sql               # 数据库建表+初始化数据
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/ganttchart/
│       │   ├── GanttChartApplication.java   # 启动入口
│       │   ├── config/
│       │   │   ├── CorsConfig.java          # 跨域配置
│       │   │   └── CacheConfig.java         # Caffeine 缓存配置
│       │   ├── controller/
│       │   │   ├── TaskController.java      # 任务 CRUD 接口
│       │   │   └── GanttController.java     # 甘特图数据接口
│       │   ├── entity/
│       │   │   ├── Task.java                # 任务实体
│       │   │   └── Holiday.java             # 节假日实体
│       │   ├── mapper/
│       │   │   ├── TaskMapper.java
│       │   │   └── HolidayMapper.java
│       │   ├── dto/
│       │   │   ├── ApiResult.java           # 统一响应封装
│       │   │   └── GanttDataVO.java         # 甘特图响应 VO
│       │   └── service/
│       │       ├── TaskService.java
│       │       ├── GanttService.java
│       │       ├── HolidayService.java
│       │       └── impl/
│       │           ├── TaskServiceImpl.java
│       │           ├── GanttServiceImpl.java
│       │           └── HolidayServiceImpl.java
│       └── resources/
│           ├── application.yml              # 应用配置
│           └── mapper/
│               ├── TaskMapper.xml
│               └── HolidayMapper.xml
└── frontend/
    ├── package.json
    ├── vite.config.js                       # 含 API 代理配置
    ├── index.html
    └── src/
        ├── main.js                          # Vue 入口
        ├── App.vue                          # 主组件（视图切换+CRUD编排）
        ├── api/
        │   └── index.js                     # API 客户端
        └── components/
            ├── Toolbar.vue                  # 工具栏（录入/导出/视图切换）
            ├── TaskListView.vue             # 列表视图
            ├── GanttChart.vue               # 甘特图视图
            └── TaskDialog.vue               # 任务编辑弹窗
```

---

## API 接口

### 任务管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/tasks` | 任务列表，支持 `projectId` 和 `keyword` 参数 |
| GET | `/api/tasks/{id}` | 获取单条任务 |
| POST | `/api/tasks` | 创建任务 |
| PUT | `/api/tasks/{id}` | 更新任务 |
| DELETE | `/api/tasks/{id}` | 删除任务 |
| PUT | `/api/tasks/{id}/launch` | 设上线时间（自动将状态改为 COMPLETED） |

### 甘特图数据

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/gantt/tasks` | 获取甘特图数据（按项目分组 + 节假日列表），支持 `projectId` 参数 |

**筛选逻辑**：仅返回 `plan_start_date` 和 `plan_end_date` 不为空，且 `actual_launch_date` 为 NULL 的任务。

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

---

## 常见问题

### MySQL 中文编码报错

如果导入 `init.sql` 时遇到中文编码问题，改用 `--default-character-set=utf8mb4`：

```bash
mysql -u root -p --default-character-set=utf8mb4 < sql/init.sql
```

### 端口冲突

- 后端默认 `8080`，修改 `backend/src/main/resources/application.yml` 中 `server.port`
- 前端默认 `5173`，修改 `frontend/vite.config.js` 中 `server.port`

### 甘特图不显示数据

检查任务是否同时满足以下条件：
1. `plan_start_date` 不为空
2. `plan_end_date` 不为空
3. `actual_launch_date` 为 NULL

有排期但已上线的任务（actual_launch_date 有值）不会出现在甘特图中。
