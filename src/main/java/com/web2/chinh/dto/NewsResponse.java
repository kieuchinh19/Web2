package com.web2.chinh.dto;

import com.web2.chinh.entity.News;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String image;
    private String author;
    private Boolean isPublished;
    private Long viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NewsResponse fromEntity(News n) {
        return NewsResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .summary(n.getSummary())
                .content(n.getContent())
                .image(n.getImage())
                .author(n.getAuthor())
                .isPublished(n.getIsPublished())
                .viewCount(n.getViewCount())
                .publishedAt(n.getPublishedAt())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }
}
