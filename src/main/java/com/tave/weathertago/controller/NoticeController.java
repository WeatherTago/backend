package com.tave.weathertago.controller;


import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/api/notice")
    public List<Notice> getNotices() {
        return noticeService.findNotices();
    }

    @GetMapping("/api/notice/{notice_id}")
    public Notice getNotice(@PathVariable("notice_id") Long noticeId) {
        return noticeService.findOne(noticeId);
    }
}
