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
            String fid = path.getFid();  // ex: "10010"
            String tid = path.getTid();  // ex: "10000"
            String startCode = fid.substring(0, fid.length() - 1);  // ex: "1001"
            String endCode = tid.substring(0, tid.length() - 1);    // ex: "1000"
            String direction = Integer.parseInt(fid) < Integer.parseInt(tid) ? "상행" : "하행";

            // ✅ 정확한 direction 기반 station 찾기
            Station startStation = stationRepository.findByStationCodeAndLineAndDirection(startCode, route, direction)
                    .orElse(null);
            Station endStation = stationRepository.findByStationCodeAndLineAndDirection(endCode, route, direction)
                    .orElse(null);

            SubwayPathDTO.StationInfo startInfo = stationToDto(
                    path.getFname(), route, startStation, true, direction, queryTime, congestionQueryService);
            SubwayPathDTO.StationInfo endInfo = stationToDto(
                    path.getTname(), route, endStation, true, direction, queryTime, congestionQueryService);

            // 중간역들 (stationCode 기준만으로 조회)
            List<String> stationCodesInPath = getIntermediateCodes(startCode, endCode);
            List<Station> stationsInPath = stationRepository.findByLineAndStationCodeIn(route, stationCodesInPath);

            Map<String, Station> stationMap = stationsInPath.stream()
                    .collect(Collectors.toMap(Station::getStationCode, s -> s, (a, b) -> a));  // 중복 코드 처리

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