package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // id으로 Notice 리스트 조회
    Notice findByNoticeId(Long noticeId);

    boolean existsByTitle(String title); // 제목 필드명이 title일 때
}
