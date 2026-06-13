package com.ganttchart.mapper;

import com.ganttchart.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskMapper {
    List<Task> selectGanttTasks(@Param("projectId") Long projectId);

    List<Task> selectAll(@Param("projectId") Long projectId,
                         @Param("keyword") String keyword);

    Task selectById(Long id);

    int insert(Task task);

    int updateById(Task task);

    int deleteById(Long id);
}
