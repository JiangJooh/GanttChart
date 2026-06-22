package com.ganttchart.mapper;

import com.ganttchart.entity.LinkItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LinkMapper {
    List<LinkItem> selectAllOrderByPriority();

    LinkItem selectById(@Param("id") Long id);

    int insert(LinkItem linkItem);

    int updateById(LinkItem linkItem);

    int deleteById(@Param("id") Long id);
}
