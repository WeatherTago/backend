package com.tave.weathertago.service.Notice;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.NoticeHandler;
import com.tave.weathertago.converter.NoticeConverter;
import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.dto.Notice.NoticeResponseDTO;
import com.tave.weathertago.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tave.weathertago.apiPayload.code.status.ErrorStatus.NOTICE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NoticeQueryServiceImpl implements NoticeQueryService {

    private final NoticeRepository noticeRepository;

    @Override
    public NoticeResponseDTO.NoticeDetail getNoticesByNoticeId(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new NoticeHandler(ErrorStatus.NOTICE_NOT_FOUND));
        return NoticeConverter.toNoticeDetail(notice);
    }

    @Override
    public List<NoticeResponseDTO.NoticeDetail> getAllNotices() {
        List<Notice> noticeList = noticeRepository.findAllByOrderByNoticeIdDesc();
        if (noticeList.isEmpty()) {
            throw new NoticeHandler(ErrorStatus.NOTICE_NOT_FOUND);
        }
        return noticeList.stream()
                .map(NoticeConverter::toNoticeDetail)
                .collect(Collectors.toList());

    }
}

