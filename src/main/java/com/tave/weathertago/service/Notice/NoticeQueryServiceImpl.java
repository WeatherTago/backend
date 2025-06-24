package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeQueryServiceImpl implements NoticeQueryService {

    private final NoticeRepository noticeRepository;

    @Override
    public Optional<Notice> getNoticesByNoticeId(Long noticeId) { return noticeRepository.findByNoticeId(noticeId); }

    @Override
    public List<Notice> getAllNotices() { return noticeRepository.findAllByOrderByNoticeIdDesc();

    }
}

