package com.ganttchart.controller;

import com.ganttchart.dto.ApiResult;
import com.ganttchart.dto.GanttDataVO;
import com.ganttchart.service.GanttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gantt")
public class GanttController {

    @Autowired
    private GanttService ganttService;

    @GetMapping("/tasks")
    public ApiResult<GanttDataVO> getGanttTasks(
            @RequestParam(required = false) Long projectId) {
        GanttDataVO data = ganttService.getGanttData(projectId);
        return ApiResult.success(data);
    }
}
