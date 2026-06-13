package com.ganttchart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.ganttchart.mapper")
@EnableCaching
public class GanttChartApplication {
    public static void main(String[] args) {
        SpringApplication.run(GanttChartApplication.class, args);
    }
}
