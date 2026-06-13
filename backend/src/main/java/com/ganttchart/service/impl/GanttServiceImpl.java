package com.ganttchart.service.impl;

import com.ganttchart.dto.GanttDataVO;
import com.ganttchart.dto.GanttDataVO.ProjectGroup;
import com.ganttchart.dto.GanttDataVO.TaskVO;
import com.ganttchart.entity.Task;
import com.ganttchart.mapper.TaskMapper;
import com.ganttchart.service.GanttService;
import com.ganttchart.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GanttServiceImpl implements GanttService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private HolidayService holidayService;

    @Override
    public GanttDataVO getGanttData(Long projectId) {
        List<Task> tasks = taskMapper.selectGanttTasks(projectId);

        // Group tasks by project
        Map<Long, List<Task>> grouped = tasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getProjectId() != null ? t.getProjectId() : -1L,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ProjectGroup> projectGroups = new ArrayList<>();
        LocalDate earliest = null, latest = null;

        for (Map.Entry<Long, List<Task>> entry : grouped.entrySet()) {
            ProjectGroup pg = new ProjectGroup();
            Task first = entry.getValue().get(0);
            pg.setProjectId(first.getProjectId());
            pg.setProjectName(first.getProjectName() != null ? first.getProjectName() : "未分类");

            List<TaskVO> taskVOs = new ArrayList<>();
            for (Task t : entry.getValue()) {
                // Track date range
                if (t.getPlanStartDate() != null &&
                        (earliest == null || t.getPlanStartDate().isBefore(earliest))) {
                    earliest = t.getPlanStartDate();
                }
                if (t.getPlanEndDate() != null &&
                        (latest == null || t.getPlanEndDate().isAfter(latest))) {
                    latest = t.getPlanEndDate();
                }

                TaskVO vo = new TaskVO();
                vo.setTaskId(t.getId());
                vo.setTaskName(t.getName());
                vo.setAssignee(t.getAssignee());
                vo.setPlanStartDate(t.getPlanStartDate() != null ? t.getPlanStartDate().toString() : null);
                vo.setPlanEndDate(t.getPlanEndDate() != null ? t.getPlanEndDate().toString() : null);
                vo.setStatus(t.getStatus());
                vo.setPriority(t.getPriority());
                taskVOs.add(vo);
            }

            pg.setTasks(taskVOs);
            projectGroups.add(pg);
        }

        // Compute date range with padding
        LocalDate rangeStart = earliest != null ? earliest.minusDays(3) : LocalDate.now();
        LocalDate rangeEnd = latest != null ? latest.plusDays(7) : LocalDate.now().plusDays(14);

        // Get holidays for the range
        List<String> holidays = holidayService.getHolidayDateStrings(rangeStart, rangeEnd);

        GanttDataVO vo = new GanttDataVO();
        vo.setProjects(projectGroups);
        vo.setHolidays(holidays);
        return vo;
    }
}
