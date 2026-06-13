package com.ganttchart.dto;

import lombok.Data;
import java.util.List;

@Data
public class GanttDataVO {
    private List<ProjectGroup> projects;
    private List<String> holidays;

    @Data
    public static class ProjectGroup {
        private Long projectId;
        private String projectName;
        private List<TaskVO> tasks;
    }

    @Data
    public static class TaskVO {
        private Long taskId;
        private String taskName;
        private String assignee;
        private String planStartDate;
        private String planEndDate;
        private String status;
        private String priority;
    }
}
