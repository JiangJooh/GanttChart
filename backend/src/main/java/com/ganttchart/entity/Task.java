package com.ganttchart.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private String name;
    private Long projectId;
    private String projectName;
    private String assignee;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private LocalDate actualLaunchDate;
    private String status;
    private String priority;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
