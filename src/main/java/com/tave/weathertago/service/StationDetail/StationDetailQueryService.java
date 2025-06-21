package com.tave.weathertago.service.StationDetail;

import com.tave.weathertago.dto.StationDetail.StationDetailResponseDTO;

public interface StationDetailQueryService {
    StationDetailResponseDTO.Response getDetail(String name, String line);
}
