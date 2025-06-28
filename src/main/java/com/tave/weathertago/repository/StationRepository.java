package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station,Long> {
    List<Station> findAllByName(String name);

    Optional<Station> findByNameAndLine(String name, String line);

    List<Station> findAllByNameAndLine(String name, String line);

    boolean existsByNameAndLine(String name, String line);

    Optional<Station> findFirstByName(String name);

}
