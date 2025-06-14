package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Notice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {

    private final EntityManager em;

    public void save(Notice notice) {
        em.persist(notice);
    }

    public Notice findOne(Long notice_id) {
        return em.find(Notice.class, notice_id);
    }

    public List<Notice> findAll() {
        return em.createQuery("select n from Notice n", Notice.class).getResultList();
    }

    public List<Notice> findByTitle(String title) {
        return em.createQuery("select n from Notice n where n.title = :title", Notice.class).setParameter("title", title).getResultList();
    }
}
