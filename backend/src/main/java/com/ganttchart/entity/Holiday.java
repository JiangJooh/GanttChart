package com.ganttchart.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Holiday {
    private Long id;
    private LocalDate holidayDate;
    private String holidayName;
    private Integer holidayType;
    private Integer year;
    private LocalDateTime createTime;
}
