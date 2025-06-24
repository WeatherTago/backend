package com.tave.weathertago.dto.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

}
