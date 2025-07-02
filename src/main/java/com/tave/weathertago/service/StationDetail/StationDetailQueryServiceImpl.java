package com.tave.weathertago.service.StationDetail;

/*
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

        WeatherDTO weather = weatherApiClient.getWeather(name);


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

 */


