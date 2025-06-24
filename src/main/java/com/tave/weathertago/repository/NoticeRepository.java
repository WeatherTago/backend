package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // id으로 Notice 리스트 조회
    Optional<Notice> findByNoticeId(Long noticeId);

    List<Notice> findAllByOrderByNoticeIdDesc(); // id 내림차순으로 정렬 (최신 공지사항부터)

}
