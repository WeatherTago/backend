package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.dto.station.SubwayPathResponseDTO;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.service.congestion.CongestionQueryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubwayPathConverter {

    public static Optional<SubwayPathDTO> from(
            SubwayPathResponseDTO response,
            StationRepository stationRepository,
            CongestionQueryService congestionQueryService,
            LocalDateTime queryTime
    ) {
        if (response == null || response.getMsgBody() == null || response.getMsgBody().getItemList().isEmpty()) {
            return Optional.empty();
        }

        SubwayPathResponseDTO.Item bestItem = response.getMsgBody().getItemList().get(0);
        List<SubwayPathDTO.SubwayStepDto> steps = new ArrayList<>();

        for (SubwayPathResponseDTO.Path path : bestItem.getPathList()) {
            String route = path.getRouteNm();
            String fid = path.getFid();  // 예: "10010"
            String tid = path.getTid();  // 예: "10000"

            //복원
            String startCode = String.valueOf(Integer.parseInt(fid) / 10);  // "03010" → 301
            String endCode = String.valueOf(Integer.parseInt(tid) / 10);    // "10000" → 1000

            String direction;
            if (route.equals("2호선")) {
                direction = Integer.parseInt(fid) < Integer.parseInt(tid) ? "외선" : "내선";
            } else {
                direction = Integer.parseInt(fid) < Integer.parseInt(tid) ? "상행" : "하행";
            }

            // ✅ 중간역 코드 먼저 생성
            List<String> stationCodesInPath = getIntermediateCodes(startCode, endCode);

            // ✅ 방향 포함 정확한 출발/도착역 찾기
            Station startStation = stationRepository.findByStationCodeAndLineAndDirection(startCode, route, direction)
                    .orElse(null);
            Station endStation = stationRepository.findByStationCodeAndLineAndDirection(endCode, route, direction)
                    .orElse(null);

            // ✅ 중간역들도 방향 포함 조회
            List<Station> stationsInPath = stationRepository.findByStationCodeInAndLineAndDirection(
                    stationCodesInPath, route, direction
            );

            // ✅ 시작/끝역 정보 DTO 변환
            SubwayPathDTO.StationInfo startInfo = stationToDto(
                    path.getFname(), route, startStation, true, direction, queryTime, congestionQueryService);
            SubwayPathDTO.StationInfo endInfo = stationToDto(
                    path.getTname(), route, endStation, true, direction, queryTime, congestionQueryService);

            // ✅ 중간역은 혼잡도/방향 없이
            Map<String, Station> stationMap = stationsInPath.stream()
                    .collect(Collectors.toMap(Station::getStationCode, s -> s, (a, b) -> a));

            List<SubwayPathDTO.StationInfo> allStations = stationCodesInPath.stream()
                    .map(code -> {
                        Station s = stationMap.get(code);
                        return SubwayPathDTO.StationInfo.builder()
                                .stationId(s != null ? String.valueOf(s.getId()) : null)
                                .stationName(s != null ? s.getName() : "(Unknown)")
                                .line(route)
                                .build();
                    })
                    .collect(Collectors.toList());

            steps.add(SubwayPathDTO.SubwayStepDto.builder()
                    .line(route)
                    .startStation(startInfo)
                    .endStation(endInfo)
                    .allStations(allStations)
                    .build());
        }

        return Optional.of(SubwayPathDTO.builder()
                .totalTime(bestItem.getTime())
                .totalDistance(bestItem.getDistance())
                .steps(steps)
                .build());
    }

    private static SubwayPathDTO.StationInfo stationToDto(
            String name, String line, Station station,
            boolean includeCongestion,
            String direction,
            LocalDateTime queryTime, CongestionQueryService congestionQueryService
    ) {
        PredictionResponseDTO congestion = null;
        if (includeCongestion && station != null) {
            congestion = congestionQueryService.getCongestion(station.getId(), queryTime);
        }

        return SubwayPathDTO.StationInfo.builder()
                .stationId(station != null ? String.valueOf(station.getId()) : null)
                .stationName(station != null ? station.getName() : name)
                .line(station != null ? station.getLine() : line)
                .direction(direction)
                .congestion(congestion)
                .build();
    }

    private static List<String> getIntermediateCodes(String startCode, String endCode) {
        int start = Integer.parseInt(startCode);
        int end = Integer.parseInt(endCode);

        List<String> codes = new ArrayList<>();
        if (start <= end) {
            for (int i = start; i <= end; i++) {
                codes.add(String.format("%04d", i));
            }
        } else {
            for (int i = start; i >= end; i--) {
                codes.add(String.format("%04d", i));
            }
        }
        return codes;
    }
}
