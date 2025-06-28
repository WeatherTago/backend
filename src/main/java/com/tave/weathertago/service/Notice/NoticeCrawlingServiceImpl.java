package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.dto.Notice.NoticeResponseDTO;
import com.tave.weathertago.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NoticeCrawlingServiceImpl implements NoticeCrawlingService {

    private final NoticeRepository noticeRepository;

    @Override
    public NoticeResponseDTO.NoticeCrawlingResult crawlAndSaveNotices() {
        List<NoticeResponseDTO.NoticeDetail> createdNotices = new ArrayList<>();
        List<NoticeResponseDTO.NoticeDetail> updatedNotices = new ArrayList<>();
        List<NoticeResponseDTO.NoticeDetail> unchangedNotices = new ArrayList<>();

        int totalCount = 0;

        try {
            String listApiUrl = "https://topis.seoul.go.kr/notice/selectNoticeList.do";
            Map<String, String> data = new HashMap<>();
            data.put("pageIndex", "1");
            data.put("recordPerPage", "10");
            data.put("pageSize", "5");
            data.put("jsFunction", "fn_getNoticeList");
            data.put("bdwrSeq", "");
            data.put("blbdDivCd", "02");
            data.put("bdwrDivCd", "");
            data.put("tabGubun", "A");
            data.put("category", "sTtl");
            data.put("boardSearch", "");

            Connection.Response response = Jsoup.connect(listApiUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .ignoreContentType(true) // json을 받아오려면 타입 무시 해야함
                    .method(Connection.Method.POST)
                    .data(data)
                    .timeout(60000) // 60초
                    .execute();

            JSONObject json = new JSONObject(response.body());
            JSONArray rows = json.getJSONArray("rows");
            totalCount = rows.length();

            List<JSONObject> rowList = new ArrayList<>();
            for (int i = 0; i < rows.length(); i++) {
                rowList.add(rows.getJSONObject(i));
            }

            // bdwrSeq 내림차순 정렬
            rowList.sort((a, b) -> Long.compare(
                    Long.parseLong(b.getString("bdwrSeq")),
                    Long.parseLong(a.getString("bdwrSeq"))
            ));

            int createdCount = 0;
            int updatedCount = 0;
            int unchangedCount = 0;

            for (JSONObject row : rowList) {
                Long noticeId = Long.parseLong(row.getString("bdwrSeq"));
                String title = row.getString("bdwrTtlNm");
                String content = row.optString("bdwrCts", "");
                String createAt = row.optString("createDate", "");
                String updateAt = row.optString("updateDate", "");

                NoticeResponseDTO.NoticeDetail detail = NoticeResponseDTO.NoticeDetail.builder()
                        .noticeId(noticeId)
                        .title(title)
                        .content(content)
                        .createdAt(createAt)
                        .updatedAt(updateAt)
                        .build();

                Optional<Notice> existing = noticeRepository.findByNoticeId(noticeId);

                if (existing.isEmpty()) {
                    // 신규 저장
                    Notice notice = Notice.builder()
                            .noticeId(noticeId)
                            .title(title)
                            .content(content)
                            .createAt(createAt)
                            .updateAt(updateAt)
                            .build();
                    noticeRepository.save(notice);
                    createdNotices.add(detail);
                } else {
                    Notice notice = existing.get();
                    // 기존 DB의 updateAt과 크롤링한 updateAt이 다를 때만 업데이트
                    if (!Objects.equals(updateAt, notice.getUpdateAt())) {
                        // 업데이트된 공지 다시 DB에 저장!
                        notice.setTitle(title);
                        notice.setContent(content);
                        notice.setCreateAt(createAt);
                        notice.setUpdateAt(updateAt);
                        noticeRepository.save(notice);
                        updatedNotices.add(detail);
                    } else {
                        // 변경 없음
                        System.out.println("변경 없음: " + title);
                        unchangedNotices.add(detail);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return NoticeResponseDTO.NoticeCrawlingResult.builder()
                .createdNotices(createdNotices)
                .updatedNotices(updatedNotices)
                .unchangedNotices(unchangedNotices)
                .totalCount(totalCount)
                .build();
    }

    // 1시간마다 크롤링 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 0/1 * * *", zone = "Asia/Seoul")
    public void scheduledCrawlAndSaveNotices() {
        System.out.println("스케줄러에 의해 공지사항 크롤링 시작: " + java.time.LocalDateTime.now());
        crawlAndSaveNotices();
    }
}
