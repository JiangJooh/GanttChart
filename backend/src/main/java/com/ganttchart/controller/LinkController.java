package com.ganttchart.controller;

import com.ganttchart.dto.ApiResult;
import com.ganttchart.entity.LinkItem;
import com.ganttchart.service.LinkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public ApiResult<List<LinkItem>> list() {
        return ApiResult.success(linkService.listAll());
    }

    @PostMapping
    public ApiResult<LinkItem> save(@RequestBody LinkItem linkItem) {
        return ApiResult.success(linkService.save(linkItem));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        linkService.delete(id);
        return ApiResult.success(null);
    }
}
