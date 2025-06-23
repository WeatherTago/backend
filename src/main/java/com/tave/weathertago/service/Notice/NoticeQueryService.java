package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;

import java.util.List;

public interface NoticeQueryService {

    List<Notice> getNoticesByTitle(String title);

    List<Notice> getAllNotices();

    List<Notice> getNoticesById(Long noticeId);
}
