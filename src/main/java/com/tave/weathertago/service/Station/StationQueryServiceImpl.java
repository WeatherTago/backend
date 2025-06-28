package com.tave.weathertago.service.Station;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
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
        List<Station> result = stationRepository.findAllByName(name);

        if (result.isEmpty()) {
            throw new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND);
        }

        return result;
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }


    /**
     * 역 이름 + 호선으로 정확히 일치하는 역 코드 조회
     * - DB에 동일한 name + line 조합이 여러 개 있으면 예외 발생
     */
    public String getStationCodeByNameAndLine(String name, String line) {
        // 먼저 이름만으로 조회해서 역이 존재하는지 확인
        List<Station> byName = stationRepository.findAllByName(name);
        if (byName.isEmpty()) {
            throw new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND); // 이름 자체가 없음
        }

        // 이름+호선으로 다시 조회
        List<Station> results = byName.stream()
                .filter(s -> s.getLine().equals(line))
                .toList();

        if (results.isEmpty()) {
            throw new StationHandler(ErrorStatus.STATION_LINE_NOT_FOUND); // 이름은 있지만 호선이 안 맞음
        }

        if (results.size() > 1) {
            throw new StationHandler(ErrorStatus.STATION_LINE_NOT_FOUND); // 동일한 name+line 조합이 여러 개면 중복 오류
        }

        return results.get(0).getStationCode();
    }

}
