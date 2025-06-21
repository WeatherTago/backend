package com.tave.weathertago.controller;

import com.tave.weathertago.dto.TimeTableDTO;
import com.tave.weathertago.infrastructure.TimetableApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timetable/test")
public class TimetableTestController {

    private final TimetableApiClient timetableApiClient;

    /**
     * 시간표 원본 JSON을 확인하는 테스트용 API
     */
    @GetMapping("/raw")
    public String getRawJson(
            @RequestParam String stationCode,
            @RequestParam String weekTag,
            @RequestParam String inoutTag
    ) {
        // 내부적으로 실제 요청 후 원문 JSON만 받아오는 메서드를 만들지 않았으므로 임시로 직접 구성
        return timetableApiClient.getTimetable(stationCode, weekTag, inoutTag).toString();
    }

    /**
     * 파싱된 시간표 리스트를 확인하는 테스트용 API
     */
    @GetMapping("/parsed")
    public List<TimeTableDTO> getParsedTimetable(
            @RequestParam String stationCode,
            @RequestParam String weekTag,
            @RequestParam String inoutTag
    ) {
        return timetableApiClient.getTimetable(stationCode, weekTag, inoutTag);
    }
}