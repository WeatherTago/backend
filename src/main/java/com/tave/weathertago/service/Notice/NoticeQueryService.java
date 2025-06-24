package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;

import java.util.List;
import java.util.Optional;

public interface NoticeQueryService {

    Optional<Notice> getNoticesByNoticeId(Long noticeId);

    List<Notice> getAllNotices();

}
