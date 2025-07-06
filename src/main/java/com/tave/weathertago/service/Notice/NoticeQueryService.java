package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.dto.Notice.NoticeResponseDTO;

import java.util.List;
import java.util.Optional;

public interface NoticeQueryService {

    NoticeResponseDTO.NoticeDetail getNoticesByNoticeId(Long noticeId);

    List<NoticeResponseDTO.NoticeDetail> getAllNotices();

}
