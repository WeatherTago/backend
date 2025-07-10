package com.tave.weathertago.apiPayload.code.status;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 로그인 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4011", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4012", "토큰이 만료되었습니다."),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH4013", "토큰 서명이 유효하지 않습니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4014", "형식이 잘못된 토큰입니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4041", "사용자를 찾을 수 없습니다."),

    // 역 관려 에러
    STATION_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATION4001", "해당 stationId에 대한 역을 찾을 수 없습니다."),
    STATION_NAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATION4002", "해당 역을 찾을 수 없습니다."),
    STATION_LINE_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATION4003", "해당 호선을 찾을 수 없습니다."),
    INVALID_DIRECTION(HttpStatus.BAD_REQUEST, "STATION4004", "잘못된 direction입니다."),

    NO_SUBWAY_ROUTE_FOUND(HttpStatus.BAD_REQUEST, "STATION4006", "해당 경로를 찾을 수 없습니다."),

    // 즐겨찾기 관련 애러
    FAVORITE_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATION4004", "즐겨찾기를 찾을 수 없습니다."),

    // 경로 관련 에러
    PATH_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATION4005", "해당 경로를 찾을 수 없습니다."),

    //csv읽기 실패
    FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "STATION5002", "CSV 파일을 읽는 중 오류가 발생했습니다."),

    // 날씨 관련 에러
    WEATHER_API_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY, "WEATHER4001", "기상청 응답이 비어 있습니다."),
    WEATHER_API_FAIL(HttpStatus.BAD_GATEWAY, "WEATHER4002", "기상청 API 요청에 실패했습니다."),
    WEATHER_API_PARSE_ERROR(HttpStatus.BAD_GATEWAY, "WEATHER4003", "기상청 응답 파싱에 실패했습니다."),
    WEATHER_API_INVALID_STRUCTURE(HttpStatus.BAD_GATEWAY, "WEATHER4004", "기상청 응답 구조가 잘못되었습니다."),
    WEATHER_CACHE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "WEATHER5001", "날씨 정보를 Redis에 캐싱하는 중 오류가 발생했습니다."),

    // AI 관련 에러
    AI_PREDICTION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AI5001", "AI 요청에 실패했습니다."),

    // 공지사항 관련 에러
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE4041", "공지사항을 찾을 수 없습니다."),
    NOTICE_CRAWLING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE5001", "공지사항 크롤링에 실패했습니다."),

    // 알림 관련 에러
    ALARM_INVALID_INPUT(HttpStatus.BAD_REQUEST, "NOTICE4001", "잘못된 요청입니다."),
    ALARM_FORBIDDEN(HttpStatus.FORBIDDEN, "NOTICE4031", "권한이 없습니다."),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE4041", "알림을 찾을 수 없습니다."),
    ALARM_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE5001", "알림 생성에 실패했습니다."),
    ALARM_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE5002", "알림 수정에 실패했습니다."),
    ALARM_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE5003", "알림 삭제에 실패했습니다."),
    ALARM_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE5004", "알림(Firebase) 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
