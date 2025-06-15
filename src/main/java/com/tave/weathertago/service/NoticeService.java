package com.tave.weathertago.service;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void save(Notice notice) {
        noticeRepository.save(notice);
    }

    @Transactional
    public void updateNotice(Long notice_id, String title, String content) {
        Notice notice = noticeRepository.findOne(notice_id);
        if (notice == null) {
            throw new IllegalStateException("해당 ID의 공지가 존재하지 않습니다." + notice_id);
        }
        notice.setTitle(title);
        notice.setContent(content);
    }

    public List<Notice> findNotices() {
        return noticeRepository.findAll();
    }

    public Notice findOne(Long notice_id) {
        return noticeRepository.findOne(notice_id);
    }
}
