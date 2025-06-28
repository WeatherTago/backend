package com.tave.weathertago.dto.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class NoticeResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class NoticeDetail {
        Long noticeId;
        String title;
        String content;
        String createdAt;
        String updatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class NoticeCrawlingResult {
        List<NoticeDetail> createdNotices;
        List<NoticeDetail> updatedNotices;
        List<NoticeDetail> unchangedNotices;
        int totalCount;
    }

}
