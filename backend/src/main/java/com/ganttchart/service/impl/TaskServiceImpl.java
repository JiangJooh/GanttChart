package com.ganttchart.service.impl;

import com.ganttchart.entity.Task;
import com.ganttchart.mapper.TaskMapper;
import com.ganttchart.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<Task> listTasks(Long projectId, String keyword) {
        return taskMapper.selectAll(projectId, keyword);
    }

    @Override
    public Task getById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public Task create(Task task) {
        taskMapper.insert(task);
        return task;
    }

    @Override
    public Task update(Task task) {
        taskMapper.updateById(task);
        return taskMapper.selectById(task.getId());
    }

    @Override
    public void delete(Long id) {
        taskMapper.deleteById(id);
    }

    @Override
    public Task setLaunchDate(Long id, String launchDate) {
        Task task = taskMapper.selectById(id);
        if (task != null && launchDate != null) {
            task.setActualLaunchDate(LocalDate.parse(launchDate));
            task.setStatus("COMPLETED");
            taskMapper.updateById(task);
        }
        return taskMapper.selectById(id);
    }
}
