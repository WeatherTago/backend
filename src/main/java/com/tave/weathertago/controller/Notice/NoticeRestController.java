package com.tave.weathertago.controller.Notice;


import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.converter.NoticeConverter;
import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.dto.Notice.NoticeResponseDTO;
import com.tave.weathertago.service.Notice.NoticeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Tag(name = "Notice", description = "공지사항 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeRestController {

    private final NoticeQueryService noticeQueryService;

    @Operation(summary = "공지사항 전체 조회", description = "DB에 저장된 모든 공지사항을 최신순으로 조회합니다.")
    @GetMapping("")
    public ApiResponse<List<NoticeResponseDTO.NoticeDetail>> getNotices() {
        List<NoticeResponseDTO.NoticeDetail> noticeDetails = noticeQueryService.getAllNotices();
        return ApiResponse.onSuccess(noticeDetails);
    }

    @Operation(summary = "특정 공지사항 조회", description = "특정 공지사항을 id로 조회합니다.")
    @GetMapping("/{notice_id}")
    public ApiResponse<NoticeResponseDTO.NoticeDetail> getNotice(@PathVariable("noticeId") Long noticeId) {
        NoticeResponseDTO.NoticeDetail noticeDetail = noticeQueryService.getNoticesByNoticeId(noticeId);
        return ApiResponse.onSuccess(noticeDetail);
    }
}
