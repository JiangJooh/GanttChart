package com.ganttchart.mapper;

import com.ganttchart.entity.Memo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MemoMapper {
    Memo selectByDate(@Param("memoDate") String memoDate);
    List<Memo> selectAllOrderByDateDesc();
    int upsert(Memo memo);
    int insert(Memo memo);
    int updateById(Memo memo);
}
