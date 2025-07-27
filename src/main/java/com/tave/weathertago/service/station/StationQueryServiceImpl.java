package com.tave.weathertago.service.station;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.converter.StationConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.service.congestion.CongestionQueryService;
import com.tave.weathertago.service.weather.WeatherQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationQueryServiceImpl implements StationQueryService {

    private final StationRepository stationRepository;
    private final WeatherQueryService weatherQueryService;
    private final CongestionQueryService congestionQueryService;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /*
    @Override
    @Transactional
    public StationResponseDTO.JoinResultDTO getStationByNameAndLine(String name, String line, LocalDateTime time) {
        Station station = stationRepository.findByNameAndLine(name, line)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));

        WeatherDTO weather = weatherQueryService.getWeather(station.getLatitude(), station.getLongitude(), time);
        CongestionDTO congestion = congestionQueryService.getCongestion(station.getStationCode(), time);

        return StationConverter.toJoinResultDTO(station, weather, congestion);
    }

     */

    @Override
    @Transactional
    public StationResponseDTO.JoinResultDTO getStationById(Long stationId, LocalDateTime time) {
        Station baseStation = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_ID_NOT_FOUND));

        WeatherResponseDTO weather = weatherQueryService.getWeather(stationId, time);

        List<Station> stations = stationRepository.findAllByNameAndLine(baseStation.getName(), baseStation.getLine());

        // 3. 방향별 혼잡도 Map 생성
        Map<String, StationResponseDTO.DirectionalStationDTO> congestionByDirection = new HashMap<>();

        for (Station s : stations) {
            PredictionResponseDTO congestion = congestionQueryService.getCongestion(s.getId(), time);
            StationResponseDTO.DirectionalStationDTO dto = StationConverter.toDirectionalStationDTO(s, congestion);
            congestionByDirection.put(s.getDirection(), dto);
        }

        return StationConverter.toJoinResultDTO(baseStation, weather, congestionByDirection);
    }

    @Override
    @Transactional
    public List<StationResponseDTO.SimpleStationDTO> getAllSimpleStations() {
        List<Station> allStations = stationRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        Map<String, Station> representativeStations = allStations.stream()
                .collect(Collectors.toMap(
                        s -> s.getName() + "::" + s.getLine(),
                        s -> s,
                        (existing, incoming) -> compareDirectionPriority(existing.getDirection()) <= compareDirectionPriority(incoming.getDirection())
                                ? existing : incoming
                ));

        // Map의 값들도 id 순 정렬
        return representativeStations.values().stream()
                .sorted(Comparator.comparing(Station::getId))
                .map(StationConverter::toSimpleDTO)
                .toList();
    }

    private int compareDirectionPriority(String direction) {
        return switch (direction) {
            case "외선" -> 1;
            case "상행" -> 2;
            case "하행" -> 3;
            case "내선" -> 4;
            default -> 99;
        };
    }

    @Override
    @Transactional
    public List<StationResponseDTO.StationInfoDTO> getAllStationsInfo() {
        List<Station> allStations = stationRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        Map<String, Station> representativeStations = allStations.stream()
                .collect(Collectors.toMap(
                        s -> s.getName() + "::" + s.getLine(),
                        s -> s,
                        (existing, incoming) -> compareDirectionPriority(existing.getDirection()) <= compareDirectionPriority(incoming.getDirection())
                                ? existing : incoming
                ));

        // Map의 값들도 id 순 정렬
        return representativeStations.values().stream()
                .sorted(Comparator.comparing(Station::getId))
                .map(StationConverter::toStationInfoDTO)
                .toList();
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

    @Override
    @Transactional
    public Map<String, StationResponseDTO.DirectionalData> getStatus(Long stationId) {
        long totalStart = System.currentTimeMillis();

        Station baseStation = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_ID_NOT_FOUND));

        List<Station> stations = stationRepository.findAllByNameAndLine(baseStation.getName(), baseStation.getLine());

        Map<String, StationResponseDTO.DirectionalData> directionMap = new HashMap<>();
        int totalCount = 0;

        for (Station s : stations) {
            List<StationResponseDTO.TimedWeatherDTO> weathers = new ArrayList<>();
            List<StationResponseDTO.TimedCongestionDTO> congestions = new ArrayList<>();

            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1);
            LocalDateTime end = now.toLocalDate().plusDays(3).atTime(0, 0);

            for (LocalDateTime dt = now; !dt.isAfter(end); dt = dt.plusHours(1)) {
                if (dt.getHour() >= 1 && dt.getHour() <= 4) continue;

                WeatherResponseDTO weather = weatherQueryService.getWeather(s.getId(), dt);
                PredictionResponseDTO prediction = congestionQueryService.getCongestion(s.getId(), dt);

                weathers.add(StationResponseDTO.TimedWeatherDTO.builder()
                        .datetime(dt.format(DATETIME_FMT))
                        .weather(weather)
                        .build());

                congestions.add(StationResponseDTO.TimedCongestionDTO.builder()
                        .datetime(dt.format(DATETIME_FMT))
                        .prediction(prediction)
                        .build());

                totalCount++;
            }

            StationResponseDTO.DirectionalData data =
                    StationConverter.toDirectionalDataDTO(weathers, congestions);

            directionMap.put(s.getDirection(), data);
        }

        long totalEnd = System.currentTimeMillis();
        log.info("✅ 전체 응답 시간: {}ms (총 {}건 측정)", totalEnd - totalStart, totalCount);


        return directionMap;
    }
}

