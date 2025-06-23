package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.dto.Notice.NoticeResponseDTO;

import java.time.LocalDateTime;

public class NoticeConverter {

    public static NoticeResponseDTO.Response toResponse(Notice notice) {
        return NoticeResponseDTO.Response.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(LocalDateTime.now())
                .build();

    }
}
