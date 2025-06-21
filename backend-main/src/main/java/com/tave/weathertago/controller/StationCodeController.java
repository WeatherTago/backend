package com.tave.weathertago.controller;

import com.tave.weathertago.service.Station.StationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/station/code")
@RequiredArgsConstructor
public class StationCodeController {

    private final StationQueryService stationQueryService;

    @GetMapping
    public ResponseEntity<String> getStationCodeByName(@RequestParam String name) {
        String code = stationQueryService.getStationCodeByName(name);
        return ResponseEntity.ok(code);
    }

    @GetMapping("/with-line")
    public ResponseEntity<String> getStationCodeByNameAndLine(@RequestParam String name, @RequestParam String line) {
        String code = stationQueryService.getStationCodeByNameAndLine(name, line);
        return ResponseEntity.ok(code);
    }
}