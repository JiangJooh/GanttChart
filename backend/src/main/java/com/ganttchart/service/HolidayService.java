package com.ganttchart.service;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    List<String> getHolidayDateStrings(LocalDate startDate, LocalDate endDate);
}
