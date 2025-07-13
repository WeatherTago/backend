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
            String tid = path.getTid();  // 예: "03010"

            // ✅ API 코드 → DB 코드로 복원
            String startCode = restoreStationCodeFromApiCode(fid);  // "03010" → "301"
            String endCode = restoreStationCodeFromApiCode(tid);    // "10010" → "1001"

            String direction;
            if (route.equals("2호선")) {
                direction = Integer.parseInt(fid) < Integer.parseInt(tid) ? "외선" : "내선";
            } else {
                direction = Integer.parseInt(fid) < Integer.parseInt(tid) ? "상행" : "하행";
            }

            List<String> stationCodesInPath = getIntermediateCodes(startCode, endCode);

            Station startStation = stationRepository.findByStationCodeAndLineAndDirection(startCode, route, direction)
                    .orElse(null);
            Station endStation = stationRepository.findByStationCodeAndLineAndDirection(endCode, route, direction)
                    .orElse(null);

            List<Station> stationsInPath = stationRepository.findByStationCodeInAndLineAndDirection(
                    stationCodesInPath, route, direction
            );

            SubwayPathDTO.StationInfo startInfo = stationToDto(
                    path.getFname(), route, startStation, true, direction, queryTime, congestionQueryService);
            SubwayPathDTO.StationInfo endInfo = stationToDto(
                    path.getTname(), route, endStation, true, direction, queryTime, congestionQueryService);

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

    // ✅ API → DB 코드 복원
    private static String restoreStationCodeFromApiCode(String apiCode) {
        if (apiCode.length() != 5) return apiCode;
        String trimmed = apiCode.substring(0, 4); // 마지막 자리 제거
        if (trimmed.startsWith("0")) {
            return String.valueOf(Integer.parseInt(trimmed)); // 앞 0 제거 → 0301 → 301
        } else {
            return trimmed; // 1001
        }
    }

    // ✅ DB 코드 목록 생성
    private static List<String> getIntermediateCodes(String startCode, String endCode) {
        int start = Integer.parseInt(startCode);
        int end = Integer.parseInt(endCode);

        List<String> codes = new ArrayList<>();
        if (start <= end) {
            for (int i = start; i <= end; i++) {
                codes.add(String.valueOf(i)); // 3자리 or 4자리 그대로 사용
            }
        } else {
            for (int i = start; i >= end; i--) {
                codes.add(String.valueOf(i));
            }
        }
        return codes;
    }
}

