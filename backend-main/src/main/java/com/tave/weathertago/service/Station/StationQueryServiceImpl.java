package com.tave.weathertago.service.Station;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.repository.StationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationQueryServiceImpl implements StationQueryService {

    private final StationRepository stationRepository;

    @Override
    public List<Station> getStationsByName(String name) {
        return stationRepository.findAllByName(name);
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    /**
     * 역 이름으로 단일 역 코드를 조회
     * - 중복된 이름이 있으면 예외 발생
     */
    public String getStationCodeByName(String name) {
        List<Station> stations = stationRepository.findAllByName(name);

        if (stations.isEmpty()) {
            throw new EntityNotFoundException("해당 역을 찾을 수 없습니다: " + name);
        }
        if (stations.size() > 1) {
            throw new IllegalStateException("중복된 역 이름입니다. 호선 정보를 함께 제공해주세요: " + name);
        }

        return stations.get(0).getStationCode();
    }

    /**
     * 역 이름 + 호선으로 정확히 일치하는 역 코드 조회
     * - DB에 동일한 name + line 조합이 여러 개 있으면 예외 발생
     */
    public String getStationCodeByNameAndLine(String name, String line) {
        List<Station> results = stationRepository.findAllByNameAndLine(name, line);

        System.out.println("조회된 개수: " + results.size());
        results.forEach(s -> System.out.println("→ " + s.getName() + " / " + s.getLine() + " / " + s.getStationCode()));

        if (results.isEmpty()) {
            throw new EntityNotFoundException("해당 역(호선 포함)을 찾을 수 없습니다: " + name + " / " + line);
        }
        if (results.size() > 1) {
            throw new IllegalStateException("중복된 역 정보가 존재합니다: " + name + " / " + line);
        }

        return results.get(0).getStationCode();
    }

}
