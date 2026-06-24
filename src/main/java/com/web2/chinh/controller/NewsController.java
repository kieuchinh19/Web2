package com.web2.chinh.controller;

import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.NewsRequest;
import com.web2.chinh.dto.NewsResponse;
import com.web2.chinh.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    public ResponseEntity<ApiResponse<NewsResponse>> create(@Valid @RequestBody NewsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(newsService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> update(@PathVariable Long id,
            @Valid @RequestBody NewsRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", newsService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(newsService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(newsService.getAll()));
    }

    @GetMapping("/published")
    public ResponseEntity<ApiResponse<List<NewsResponse>>> getPublished() {
        return ResponseEntity.ok(ApiResponse.success(newsService.getPublished()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<NewsResponse>>> search(@RequestParam String title) {
        return ResponseEntity.ok(ApiResponse.success(newsService.searchByTitle(title)));
    }

    @GetMapping("/top-viewed")
    public ResponseEntity<ApiResponse<List<NewsResponse>>> topViewed() {
        return ResponseEntity.ok(ApiResponse.success(newsService.getTopViewed()));
    }

    @PostMapping("/{id}/increment-view")
    public ResponseEntity<ApiResponse<NewsResponse>> incrementView(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(newsService.incrementView(id)));
    }
}
