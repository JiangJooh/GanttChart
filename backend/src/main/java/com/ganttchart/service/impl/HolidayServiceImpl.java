package com.ganttchart.service.impl;

import com.ganttchart.entity.Holiday;
import com.ganttchart.mapper.HolidayMapper;
import com.ganttchart.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayMapper holidayMapper;

    @Override
    @Cacheable(value = "holidays", key = "#startDate.toString() + '_' + #endDate.toString()")
    public List<String> getHolidayDateStrings(LocalDate startDate, LocalDate endDate) {
        return holidayMapper.selectDateStringsByRange(startDate, endDate);
    }
}
