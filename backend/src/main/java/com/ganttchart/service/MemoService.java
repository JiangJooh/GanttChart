package com.ganttchart.service;

import com.ganttchart.entity.Memo;
import java.util.List;

public interface MemoService {
    Memo getByDate(String memoDate);
    List<Memo> listAll();
    Memo save(Memo memo);
}
