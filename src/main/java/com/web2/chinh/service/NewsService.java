package com.web2.chinh.service;

import com.web2.chinh.dto.NewsRequest;
import com.web2.chinh.dto.NewsResponse;

import java.util.List;

public interface NewsService {
    NewsResponse create(NewsRequest request);

    NewsResponse update(Long id, NewsRequest request);

    void delete(Long id);

    NewsResponse getById(Long id);

    List<NewsResponse> getAll();

    List<NewsResponse> getPublished();

    List<NewsResponse> searchByTitle(String title);

    List<NewsResponse> getTopViewed();

    NewsResponse incrementView(Long id);
}
