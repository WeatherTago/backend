package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station,Long> {

    boolean existsByNameAndLineAndDirection(String name, String line, String direction);

    boolean existsByName(String name);

    boolean existsByNameAndLine(String name, String line);

    Optional<Station> findByStationCodeAndLineAndDirection(String stationCode, String line, String direction);

    List<Station> findByStationCodeInAndLineAndDirection(List<String> stationCodes, String line, String direction);

    List<Station> findAllByNameAndLine(String name, String line);

    Optional<Station> findByNameAndLineAndDirection(String name, String line, String direction);

}
