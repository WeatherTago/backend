package com.tave.weathertago.service.congestion;

import com.tave.weathertago.dto.CongestionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CongestionQueryService {
    public CongestionDTO getCongestion(String stationCode, LocalDateTime time) {
        // TODO: 실제 API 연동 예정
        return new CongestionDTO("보통", 85);
    }
}