package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUser_KakaoId(String kakaoId);

}
