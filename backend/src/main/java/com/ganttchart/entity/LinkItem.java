package com.ganttchart.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LinkItem {
    private Long id;
    private String name;
    private String url;
    private Integer priority;
    private LocalDateTime createTime;
}
