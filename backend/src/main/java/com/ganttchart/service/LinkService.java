package com.ganttchart.service;

import com.ganttchart.entity.LinkItem;

import java.util.List;

public interface LinkService {
    List<LinkItem> listAll();

    LinkItem save(LinkItem linkItem);

    void delete(Long id);
}
