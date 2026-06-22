package com.ganttchart.controller;

import com.ganttchart.dto.ApiResult;
import com.ganttchart.entity.Memo;
import com.ganttchart.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    @Autowired
    private MemoService memoService;

    @GetMapping("/today")
    public ApiResult<Memo> getToday(@RequestParam String date) {
        return ApiResult.success(memoService.getByDate(date));
    }

    @GetMapping("/history")
    public ApiResult<List<Memo>> getHistory() {
        return ApiResult.success(memoService.listAll());
    }

    @PostMapping
    public ApiResult<Memo> save(@RequestBody Memo memo) {
        return ApiResult.success(memoService.save(memo));
    }
}
