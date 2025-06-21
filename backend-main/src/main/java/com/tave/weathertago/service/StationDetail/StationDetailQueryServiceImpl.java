package com.tave.weathertago.service.StationDetail;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.converter.StationDetailConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.StationDetail.StationDetailResponseDTO;
import com.tave.weathertago.dto.TimeTableDTO;
import com.tave.weathertago.dto.WeatherDTO;
import com.tave.weathertago.infrastructure.TimetableApiClient;
import com.tave.weathertago.infrastructure.WeatherApiClient;
import com.tave.weathertago.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationDetailQueryServiceImpl implements StationDetailQueryService {

    private final StationRepository stationRepository;
    private final WeatherApiClient weatherApiClient;
    private final TimetableApiClient timetableApiClient;

    @Override
    public StationDetailResponseDTO.Response getDetail(String name, String line) {
        Station station = stationRepository.findByNameAndLine(name, line)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NOT_FOUND));

        // 날씨 호출
        /*
        WeatherDTO weather = weatherApiClient.getWeather(name);

         */
        WeatherDTO weather = new WeatherDTO("23°C", "맑음");


        // 요일 코드 계산 (평일:1, 토:2, 일:3)
        String weekTag = getWeekTag(LocalDate.now());

        // 시간표 상/하행 각각 호출
        List<TimeTableDTO> upTimeTable = timetableApiClient.getTimetable(station.getStationCode(), weekTag, "1");
        List<TimeTableDTO> downTimeTable = timetableApiClient.getTimetable(station.getStationCode(), weekTag, "2");

        // 응답 조립
        return StationDetailConverter.toResponse(station, weather, upTimeTable, downTimeTable);
    }

    private String getWeekTag(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SATURDAY -> "2";
            case SUNDAY -> "3";
            default -> "1";
        };
    }
}

