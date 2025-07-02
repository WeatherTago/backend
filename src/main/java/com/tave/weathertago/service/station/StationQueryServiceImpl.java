package com.tave.weathertago.service.station;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.converter.StationConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.CongestionDTO;
import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.dto.WeatherDTO;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.service.congestion.CongestionQueryService;
import com.tave.weathertago.service.weather.WeatherQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationQueryServiceImpl implements StationQueryService {

    private final StationRepository stationRepository;
    private final WeatherQueryService weatherQueryService;
    private final CongestionQueryService congestionQueryService;

    @Override
    @Transactional
    public List<StationResponseDTO.JoinResultDTO> getStationsByName(String name, LocalDateTime time) {
        List<Station> result = stationRepository.findAllByName(name);

        if (result.isEmpty()) {
            throw new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND);
        }

        return result.stream()
                .map(station -> {
                    WeatherDTO weather = weatherQueryService.getWeather(station.getLatitude(), station.getLongitude(), time);
                    CongestionDTO congestion = congestionQueryService.getCongestion(station.getStationCode(), time);
                    return StationConverter.toJoinResultDTO(station, weather, congestion);
                })
                .toList();
    }

    @Override
    @Transactional
    public StationResponseDTO.JoinResultDTO getStationByNameAndLine(String name, String line, LocalDateTime time) {
        Station station = stationRepository.findByNameAndLine(name, line)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));

        WeatherDTO weather = weatherQueryService.getWeather(station.getLatitude(), station.getLongitude(), time);
        CongestionDTO congestion = congestionQueryService.getCongestion(station.getStationCode(), time);

        return StationConverter.toJoinResultDTO(station, weather, congestion);
    }



    /*
    /**
     * 역 이름 + 호선으로 정확히 일치하는 역 코드 조회
     * - DB에 동일한 name + line 조합이 여러 개 있으면 예외 발생

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
    */

}
