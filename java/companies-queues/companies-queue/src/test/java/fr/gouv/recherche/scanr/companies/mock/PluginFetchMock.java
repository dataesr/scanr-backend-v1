/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.mock;

import java.util.List;

import com.google.common.collect.Lists;

/**
 *
 */
public class PluginFetchMock<E> implements PluginMock<E> {
    private List<E> dtos = Lists.newArrayList();
    private MockQueueService mockQueueService;

    public PluginFetchMock(MockQueueService mockQueueService) {
        this.mockQueueService = mockQueueService;
    }

    @Override
    public Object apply(E dto) {
        this.dtos.add(dto);
        return null;
    }

    public boolean isEmpty() {
        mockQueueService.ensureFired();
        return dtos.isEmpty();
    }

    public int size() {
        mockQueueService.ensureFired();
        return dtos.size();
    }

    public E getDTO() {
        return isEmpty() ? null : dtos.get(0);
    }

    public List<E> getDTOList() {
        mockQueueService.ensureFired();
        return dtos;
    }

    public void clear() {
        mockQueueService.ensureFired();
        dtos.clear();
    }
}
