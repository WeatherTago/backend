package com.tave.weathertago.controller.Notice;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.service.Notice.NoticeCrawlingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "notice web crawling", description = "공지사항 크롤링 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")

public class NoticeCrawlingController {
    private final NoticeCrawlingService noticeCrawlingService;

    @Operation(summary = "공지사항 크롤링 진행", description = "공지사항 웹 크롤링을 진행합니다.")
    @PostMapping("/crawl-notices")
    public ApiResponse<String> crawlNotices() {
        noticeCrawlingService.crawlAndSaveNotices();
        return ApiResponse.onSuccess("공지사항 크롤링 및 저장이 완료되었습니다.");
    }
}
