package com.ganttchart.controller;

import com.ganttchart.dto.ApiResult;
import com.ganttchart.entity.Task;
import com.ganttchart.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ApiResult<List<Task>> list(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(taskService.listTasks(projectId, keyword));
    }

    @GetMapping("/{id}")
    public ApiResult<Task> getById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        if (task == null) {
            return ApiResult.error(404, "任务不存在");
        }
        return ApiResult.success(task);
    }

    @PostMapping
    public ApiResult<Task> create(@RequestBody Task task) {
        Task created = taskService.create(task);
        return ApiResult.success(created);
    }

    @PutMapping("/{id}")
    public ApiResult<Task> update(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        Task updated = taskService.update(task);
        if (updated == null) {
            return ApiResult.error(404, "任务不存在");
        }
        return ApiResult.success(updated);
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ApiResult.success(null);
    }

    @PutMapping("/{id}/launch")
    public ApiResult<Task> setLaunchDate(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String launchDate = body.get("launchDate");
        Task task = taskService.setLaunchDate(id, launchDate);
        if (task == null) {
            return ApiResult.error(404, "任务不存在");
        }
        return ApiResult.success(task);
    }
}
