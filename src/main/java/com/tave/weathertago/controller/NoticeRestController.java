package com.tave.weathertago.controller;


import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.service.Notice.NoticeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeRestController {

    private final NoticeQueryService noticeQueryService;

    @GetMapping("/")
    public List<Notice> getNotices() {
        return noticeQueryService.getAllNotices();
    }

    @GetMapping("/{noticeId}")
    public Notice getNotice(@PathVariable("noticeId") Long noticeId) {
        return (Notice) noticeQueryService.getNoticesById(noticeId);
    }
}
