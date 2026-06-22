package com.ganttchart.service.impl;

import com.ganttchart.entity.LinkItem;
import com.ganttchart.mapper.LinkMapper;
import com.ganttchart.service.LinkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkMapper linkMapper;

    public LinkServiceImpl(LinkMapper linkMapper) {
        this.linkMapper = linkMapper;
    }

    @Override
    public List<LinkItem> listAll() {
        return linkMapper.selectAllOrderByPriority();
    }

    @Override
    public LinkItem save(LinkItem linkItem) {
        if (linkItem.getId() != null) {
            linkMapper.updateById(linkItem);
        } else {
            linkMapper.insert(linkItem);
        }
        return linkItem;
    }

    @Override
    public void delete(Long id) {
        linkMapper.deleteById(id);
    }
}
