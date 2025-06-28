package com.tave.weathertago.service.Notice;

import com.tave.weathertago.dto.Notice.NoticeResponseDTO;

public interface NoticeCrawlingService {
    NoticeResponseDTO.NoticeCrawlingResult  crawlAndSaveNotices();
}
