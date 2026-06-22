package com.ganttchart.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Memo {
    private Long id;
    private LocalDate memoDate;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
