package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.dto.Notice.NoticeResponseDTO;

import java.time.LocalDateTime;

public class NoticeConverter {

    public static NoticeResponseDTO.NoticeDetail toNoticeDetail(Notice notice) {
        return NoticeResponseDTO.NoticeDetail.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreateAt())
                .updatedAt(notice.getUpdateAt())
                .build();

    }
}
