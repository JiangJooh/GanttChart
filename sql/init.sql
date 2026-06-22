-- =============================================
-- 甘特图功能 - 数据库初始化脚本
-- 使用方法: mysql -u root -p < sql/init.sql
-- =============================================

CREATE DATABASE IF NOT EXISTS `gantt_chart` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `gantt_chart`;

-- =============================================
-- 任务表
-- =============================================
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200) NOT NULL COMMENT '任务名称',
  `project_id` bigint DEFAULT NULL COMMENT '所属项目ID',
  `project_name` varchar(200) DEFAULT NULL COMMENT '所属项目名称',
  `assignee` varchar(100) DEFAULT NULL COMMENT '负责人',
  `plan_start_date` date DEFAULT NULL COMMENT '计划开始时间',
  `plan_end_date` date DEFAULT NULL COMMENT '计划结束时间',
  `actual_launch_date` date DEFAULT NULL COMMENT '实际上线/完成时间',
  `status` varchar(20) DEFAULT 'NOT_STARTED' COMMENT '任务状态: NOT_STARTED, IN_PROGRESS, COMPLETED, DELAYED',
  `priority` varchar(10) DEFAULT 'MEDIUM' COMMENT '优先级: HIGH, MEDIUM, LOW',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_plan_dates` (`plan_start_date`, `plan_end_date`),
  KEY `idx_actual_launch` (`actual_launch_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- =============================================
-- 节假日表
-- =============================================
DROP TABLE IF EXISTS `holiday`;
CREATE TABLE `holiday` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `holiday_date` date NOT NULL COMMENT '日期',
  `holiday_name` varchar(50) DEFAULT NULL COMMENT '节假日名称',
  `holiday_type` tinyint NOT NULL COMMENT '类型: 1-法定节假日, 2-调休工作日',
  `year` int DEFAULT NULL COMMENT '年份',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_date` (`holiday_date`),
  KEY `idx_year` (`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节假日配置表';

-- =============================================
-- 2026年节假日数据（国务院发布的法定节假日）
-- =============================================
INSERT INTO `holiday` (`holiday_date`, `holiday_name`, `holiday_type`, `year`) VALUES
-- 元旦
('2026-01-01', '元旦', 1, 2026),
('2026-01-02', '元旦', 1, 2026),
('2026-01-03', '元旦', 1, 2026),
-- 春节
('2026-02-15', '春节', 1, 2026),
('2026-02-16', '春节', 1, 2026),
('2026-02-17', '春节', 1, 2026),
('2026-02-18', '春节', 1, 2026),
('2026-02-19', '春节', 1, 2026),
('2026-02-20', '春节', 1, 2026),
('2026-02-21', '春节', 1, 2026),
-- 清明节
('2026-04-05', '清明节', 1, 2026),
('2026-04-06', '清明节', 1, 2026),
-- 劳动节
('2026-05-01', '劳动节', 1, 2026),
('2026-05-02', '劳动节', 1, 2026),
('2026-05-03', '劳动节', 1, 2026),
-- 端午节
('2026-06-19', '端午节', 1, 2026),
('2026-06-20', '端午节', 1, 2026),
('2026-06-21', '端午节', 1, 2026),
-- 中秋节&国庆节
('2026-10-01', '国庆节', 1, 2026),
('2026-10-02', '国庆节', 1, 2026),
('2026-10-03', '国庆节', 1, 2026),
('2026-10-04', '中秋节', 1, 2026),
('2026-10-05', '国庆节', 1, 2026),
('2026-10-06', '国庆节', 1, 2026),
('2026-10-07', '国庆节', 1, 2026),
('2026-10-08', '国庆节', 1, 2026);

-- =============================================
-- 备忘录表
-- =============================================
DROP TABLE IF EXISTS `memo`;
CREATE TABLE `memo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `memo_date` date NOT NULL COMMENT '备忘日期',
  `content` json NOT NULL COMMENT '备忘内容(JSON: [{text, done}])',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_memo_date` (`memo_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日备忘录表';

-- =============================================
-- 链接收藏表
-- =============================================
DROP TABLE IF EXISTS `link_item`;
CREATE TABLE `link_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200) NOT NULL COMMENT '链接名称',
  `url` varchar(1000) NOT NULL COMMENT '链接地址',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级排序(数字越小越靠前)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链接收藏表';

-- =============================================
-- 样例数据: 项目与任务
-- =============================================

-- 项目1: 电商平台V3.0
INSERT INTO `task` (`name`, `project_id`, `project_name`, `assignee`, `plan_start_date`, `plan_end_date`, `actual_launch_date`, `status`, `priority`) VALUES
('用户中心重构', 1, '电商平台V3.0', '张三', '2026-06-01', '2026-06-15', NULL, 'IN_PROGRESS', 'HIGH'),
('商品详情页优化', 1, '电商平台V3.0', '李四', '2026-06-05', '2026-06-20', NULL, 'NOT_STARTED', 'HIGH'),
('购物车模块改造', 1, '电商平台V3.0', '王五', '2026-06-10', '2026-06-25', NULL, 'NOT_STARTED', 'MEDIUM'),
('订单系统升级', 1, '电商平台V3.0', '张三', '2026-06-15', '2026-07-05', NULL, 'NOT_STARTED', 'HIGH'),
('支付接口对接', 1, '电商平台V3.0', '赵六', '2026-06-20', '2026-07-10', NULL, 'NOT_STARTED', 'HIGH');

-- 项目2: 数据中台建设
INSERT INTO `task` (`name`, `project_id`, `project_name`, `assignee`, `plan_start_date`, `plan_end_date`, `actual_launch_date`, `status`, `priority`) VALUES
('数据采集管道搭建', 2, '数据中台建设', '李四', '2026-05-15', '2026-06-05', '2026-06-03', 'COMPLETED', 'HIGH'),
('数据仓库建模', 2, '数据中台建设', '王五', '2026-06-01', '2026-06-20', NULL, 'IN_PROGRESS', 'HIGH'),
('ETL任务开发', 2, '数据中台建设', '赵六', '2026-06-10', '2026-07-01', NULL, 'NOT_STARTED', 'MEDIUM'),
('数据可视化大屏', 2, '数据中台建设', '张三', '2026-06-20', '2026-07-15', NULL, 'NOT_STARTED', 'MEDIUM');

-- 项目3: 内部OA系统
INSERT INTO `task` (`name`, `project_id`, `project_name`, `assignee`, `plan_start_date`, `plan_end_date`, `actual_launch_date`, `status`, `priority`) VALUES
('审批流程引擎', 3, '内部OA系统', '王五', '2026-06-01', '2026-06-18', NULL, 'IN_PROGRESS', 'HIGH'),
('考勤模块开发', 3, '内部OA系统', '赵六', '2026-06-05', '2026-06-22', NULL, 'DELAYED', 'HIGH'),
('公文管理模块', 3, '内部OA系统', '李四', '2026-06-15', '2026-07-05', NULL, 'NOT_STARTED', 'MEDIUM'),
('会议室预约', 3, '内部OA系统', '张三', '2026-06-10', '2026-06-25', NULL, 'IN_PROGRESS', 'LOW');

-- 项目4: 已上线的旧项目（不应出现在甘特图中）
INSERT INTO `task` (`name`, `project_id`, `project_name`, `assignee`, `plan_start_date`, `plan_end_date`, `actual_launch_date`, `status`, `priority`) VALUES
('旧版首页改版', 4, '已结项项目', '张三', '2026-01-10', '2026-02-01', '2026-01-28', 'COMPLETED', 'HIGH'),
('旧版登录升级', 4, '已结项项目', '李四', '2026-02-01', '2026-02-15', '2026-02-14', 'COMPLETED', 'HIGH');
