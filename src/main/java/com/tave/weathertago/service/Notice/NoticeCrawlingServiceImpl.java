package com.tave.weathertago.service.Notice;

import com.tave.weathertago.domain.Notice;
import com.tave.weathertago.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeCrawlingServiceImpl implements NoticeCrawlingService {

    private final NoticeRepository noticeRepository;

    @Override
    public void crawlAndSaveNotices() {
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
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .data(data)
                    .execute();

            JSONObject json = new JSONObject(response.body());
            JSONArray rows = json.getJSONArray("rows");

            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                String title = row.getString("bdwrTtlNm");
                String noticeId = row.getString("bdwrSeq");

                // 중복 제목 체크
                if (noticeRepository.existsByTitle(title)) {
                    System.out.println("중복 제목, 저장 건너뜀: " + title);
                    continue;
                }

                // 상세 본문은 AJAX로 받아온다
                String detailApiUrl = "https://topis.seoul.go.kr/notice/selectNotice.do";
                Map<String, String> detailParam = new HashMap<>();
                detailParam.put("blbdDivCd", "02");
                detailParam.put("bdwrSeq", noticeId);

                Connection.Response detailResponse = Jsoup.connect(detailApiUrl)
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .ignoreContentType(true)
                        .method(Connection.Method.POST)
                        .data(detailParam)
                        .execute();

                JSONObject detailJson = new JSONObject(detailResponse.body());
                JSONArray detailRows = detailJson.getJSONArray("rows");
                String content = "";
                if (detailRows.length() > 0) {
                    JSONObject detail = detailRows.getJSONObject(0);
                    content = detail.optString("bdwrCts", "");
                }

                Notice notice = new Notice();
                notice.setTitle(title);
                notice.setContent(content);
                noticeRepository.save(notice);
                System.out.println("DB 저장 성공: " + title + " / content: " + (content.length() > 30 ? content.substring(0, 30) + "..." : content));
            }
            System.out.println("크롤링 완료 - 성공: " + rows.length());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1시간마다 크롤링 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 0/1 * * *", zone = "Asia/Seoul")
    public void scheduledCrawlAndSaveNotices() {
        System.out.println("스케줄러에 의해 공지사항 크롤링 시작: " + java.time.LocalDateTime.now());
        crawlAndSaveNotices();
    }
}
