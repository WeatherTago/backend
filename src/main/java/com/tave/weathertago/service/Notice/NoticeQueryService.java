package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NoticeQueryService {

    List<Notice> getNoticesByTitle(String title);

    List<Notice> getAllNotices();

    List<Notice> getNoticesById(Long noticeId);
}
