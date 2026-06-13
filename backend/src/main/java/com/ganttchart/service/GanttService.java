package com.ganttchart.service;

import com.ganttchart.dto.GanttDataVO;

public interface GanttService {
    GanttDataVO getGanttData(Long projectId);
}
