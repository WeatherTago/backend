package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.dto.station.SubwayPathResponseDTO;
import com.tave.weathertago.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class SubwayPathConverter {

    public static Optional<SubwayPathDTO> from(
            SubwayPathResponseDTO response,
            StationRepository stationRepository
    ) {
        if (response == null || response.getMsgBody() == null || response.getMsgBody().getItemList().isEmpty()) {
            return Optional.empty();
        }

        SubwayPathResponseDTO.Item bestItem = response.getMsgBody().getItemList().get(0);
        List<SubwayPathDTO.SubwayStepDto> steps = new ArrayList<>();

        for (SubwayPathResponseDTO.Path path : bestItem.getPathList()) {
            String route = path.getRouteNm();
            String fid = path.getFid();
            String tid = path.getTid();
            String startCode = fid.substring(0, fid.length() - 1);
            String endCode = tid.substring(0, tid.length() - 1);

            Station startStation = stationRepository.findByStationCode(startCode).orElse(null);
            Station endStation = stationRepository.findByStationCode(endCode).orElse(null);

            SubwayPathDTO.StationInfo startInfo = stationToDto(fid, path.getFname(), route, startStation);
            SubwayPathDTO.StationInfo endInfo = stationToDto(tid, path.getTname(), route, endStation);

            // 중간역 ID 목록 추출 (역순도 고려)
            List<String> stationCodesInPath = getIntermediateCodes(startCode, endCode);
            List<Station> stationsInPath = stationRepository.findByLineAndStationCodeIn(route, stationCodesInPath);

            // 순서 보장을 위해 매핑
            Map<String, Station> stationMap = stationsInPath.stream()
                    .collect(Collectors.toMap(Station::getStationCode, s -> s));

            List<SubwayPathDTO.StationInfo> allStations = stationCodesInPath.stream()
                    .map(code -> {
                        Station s = stationMap.get(code);
                        String id = code + "0";
                        return SubwayPathDTO.StationInfo.builder()
                                .stationId(id)
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

    private static SubwayPathDTO.StationInfo stationToDto(String id, String name, String line, Station station) {
        return SubwayPathDTO.StationInfo.builder()
                .stationId(id)
                .stationName(station != null ? station.getName() : name)
                .line(station != null ? station.getLine() : line)
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
