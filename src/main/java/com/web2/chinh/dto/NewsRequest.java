package com.web2.chinh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không quá 255 ký tự")
    private String title;

    @Size(max = 500, message = "Tóm tắt không quá 500 ký tự")
    private String summary;

    private String content;

    @Size(max = 500, message = "URL ảnh không quá 500 ký tự")
    private String image;

    @Size(max = 100, message = "Tên tác giả không quá 100 ký tự")
    private String author;

    private Boolean isPublished;

    private LocalDateTime publishedAt;
}
