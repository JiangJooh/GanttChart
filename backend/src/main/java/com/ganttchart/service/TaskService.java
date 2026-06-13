package com.ganttchart.service;

import com.ganttchart.entity.Task;
import java.util.List;

public interface TaskService {
    List<Task> listTasks(Long projectId, String keyword);
    Task getById(Long id);
    Task create(Task task);
    Task update(Task task);
    void delete(Long id);
    Task setLaunchDate(Long id, String launchDate);
}
