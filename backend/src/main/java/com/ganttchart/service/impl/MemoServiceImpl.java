package com.ganttchart.service.impl;

import com.ganttchart.entity.Memo;
import com.ganttchart.mapper.MemoMapper;
import com.ganttchart.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemoServiceImpl implements MemoService {

    @Autowired
    private MemoMapper memoMapper;

    @Override
    public Memo getByDate(String memoDate) {
        return memoMapper.selectByDate(memoDate);
    }

    @Override
    public List<Memo> listAll() {
        return memoMapper.selectAllOrderByDateDesc();
    }

    @Override
    public Memo save(Memo memo) {
        Memo existing = memoMapper.selectByDate(memo.getMemoDate().toString());
        if (existing != null) {
            memo.setId(existing.getId());
            memoMapper.updateById(memo);
            return memo;
        } else {
            memoMapper.insert(memo);
            return memo;
        }
    }
}
