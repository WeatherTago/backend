package com.tave.weathertago.controller.stationController;

import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.service.station.SubwayPathQueryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subway")
public class SubwayPathController {

    private final SubwayPathQueryServiceImpl subwayPathService;

    @GetMapping("/path")
    public ResponseEntity<SubwayPathDTO> getPath(
            @RequestParam String start,
            @RequestParam String end) {

        SubwayPathDTO result = subwayPathService.findPath(start, end);
        return ResponseEntity.ok(result);
    }
}
