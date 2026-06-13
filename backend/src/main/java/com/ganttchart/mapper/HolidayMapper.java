package com.ganttchart.mapper;

import com.ganttchart.entity.Holiday;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HolidayMapper {
    List<Holiday> selectByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    List<String> selectDateStringsByRange(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
