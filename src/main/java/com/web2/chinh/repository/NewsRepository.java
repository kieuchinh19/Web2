package com.web2.chinh.repository;

import com.web2.chinh.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByIsPublishedTrueOrderByPublishedAtDesc();

    List<News> findByTitleContainingIgnoreCase(String title);

    List<News> findTop10ByOrderByViewCountDesc();
}
