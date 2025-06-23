package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // id으로 Notice 리스트 조회
    Notice findAllByNoticeId(Long noticeId);
}
