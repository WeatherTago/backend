package com.tave.weathertago.controller.Notice;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.service.Notice.NoticeCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")

public class NoticeCrawlingController {
    private final NoticeCrawlingService noticeCrawlingService;

    @PostMapping("/crawl-notices")
    public ApiResponse<String> crawlNotices() {
        noticeCrawlingService.crawlAndSaveNotices();
        return ApiResponse.onSuccess("공지사항 크롤링 및 저장이 완료되었습니다.");
    }
}
