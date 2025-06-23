package com.tave.weathertago.controller;


import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.converter.NoticeConverter;
import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.dto.Notice.NoticeResponseDTO;
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

    @GetMapping
    public ApiResponse<List<NoticeResponseDTO.NoticeDetail>> getNotices() {
        List<Notice> notices=noticeQueryService.getAllNotices();
        List<NoticeResponseDTO.NoticeDetail> noticeDetails = notices.stream()
                .map(NoticeConverter::toNoticeDetail)
                .toList();
        return ApiResponse.onSuccess(noticeDetails);
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeResponseDTO.NoticeDetail> getNotice(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeQueryService.getNoticesByNoticeId(noticeId);
        NoticeResponseDTO.NoticeDetail noticeDetail = NoticeConverter.toNoticeDetail(notice);
        return ApiResponse.onSuccess(noticeDetail);
    }
}
