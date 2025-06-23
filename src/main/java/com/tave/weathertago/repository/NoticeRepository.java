package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Notice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 제목으로 Notice 리스트 조회
    List<Notice> findAllByTitle(String title);

    List<Notice> findAll();

    List<Notice> findAllById(Long noticeId);
}
