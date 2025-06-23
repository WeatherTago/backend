package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;

import java.util.List;

public interface NoticeQueryService {

    Notice getNoticesByNoticeId(Long noticeId);

    List<Notice> getAllNotices();

}
