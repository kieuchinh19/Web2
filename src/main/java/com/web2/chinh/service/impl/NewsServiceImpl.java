package com.web2.chinh.service.impl;

import com.web2.chinh.dto.NewsRequest;
import com.web2.chinh.dto.NewsResponse;
import com.web2.chinh.entity.News;
import com.web2.chinh.exception.ResourceNotFoundException;
import com.web2.chinh.repository.NewsRepository;
import com.web2.chinh.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public NewsResponse create(NewsRequest request) {
        News news = News.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .content(request.getContent())
                .image(request.getImage())
                .author(request.getAuthor())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .publishedAt(request.getPublishedAt())
                .viewCount(0L)
                .build();
        return NewsResponse.fromEntity(newsRepository.save(news));
    }

    @Override
    public NewsResponse update(Long id, NewsRequest request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết id=" + id));
        news.setTitle(request.getTitle());
        news.setSummary(request.getSummary());
        news.setContent(request.getContent());
        news.setImage(request.getImage());
        news.setAuthor(request.getAuthor());
        if (request.getIsPublished() != null) {
            news.setIsPublished(request.getIsPublished());
            if (request.getIsPublished() && news.getPublishedAt() == null) {
                news.setPublishedAt(LocalDateTime.now());
            }
        }
        if (request.getPublishedAt() != null) {
            news.setPublishedAt(request.getPublishedAt());
        }
        return NewsResponse.fromEntity(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết id=" + id));
        newsRepository.delete(news);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponse getById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết id=" + id));
        return NewsResponse.fromEntity(news);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponse> getAll() {
        return newsRepository.findAll().stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponse> getPublished() {
        return newsRepository.findByIsPublishedTrueOrderByPublishedAtDesc().stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponse> searchByTitle(String keyword) {
        return newsRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponse> getTopViewed() {
        return newsRepository.findTop10ByOrderByViewCountDesc().stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NewsResponse incrementView(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết id=" + id));
        news.setViewCount((news.getViewCount() == null ? 0L : news.getViewCount()) + 1);
        return NewsResponse.fromEntity(newsRepository.save(news));
    }
}
