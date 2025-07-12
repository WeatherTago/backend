package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station,Long> {

    Optional<Station> findByNameAndLine(String name, String line);

    boolean existsByNameAndLineAndDirection(String name, String line, String direction);

    Optional<Station> findByStationCode(String stationCode);

    List<Station> findByLineAndStationCodeIn(String line, List<String> stationCodes);

    List<Station> findAllByNameAndLine(String name, String line);

    Optional<Station> findByNameAndLineAndDirection(String name, String line, String direction);

}
