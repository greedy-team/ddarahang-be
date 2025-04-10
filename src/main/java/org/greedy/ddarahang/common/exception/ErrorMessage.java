package org.greedy.ddarahang.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {

    //InvalidDataException
    MISSING_ID(HttpStatus.BAD_REQUEST, "ID가 누락되었습니다."),
    INVALID_COUNTRY_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 국가명입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "날짜 형식이 잘못되었습니다."),
    INVALID_FILTER(HttpStatus.BAD_REQUEST, "유효하지 않은 필터입니다."),

    //NotFoundDataException
    NOT_FOUND_COUNTRY(HttpStatus.NOT_FOUND, "국가 정보를 찾을 수 없습니다."),
    NOT_FOUND_REGION(HttpStatus.NOT_FOUND, "지역 정보를 찾을 수 없습니다."),
    NOT_FOUND_TRAVEL_COURSE_DETAIL(HttpStatus.NOT_FOUND, "여행 코스 상세정보를 찾을 수 없습니다."),

    // DataSyncException
    FAILED_TO_INSERT_COUNTRIES(HttpStatus.INTERNAL_SERVER_ERROR, "국가(country) 데이터를 삽입하는 데 실패했습니다."),
    FAILED_TO_INSERT_REGIONS(HttpStatus.INTERNAL_SERVER_ERROR, "지역(region) 데이터를 삽입하는 데 실패했습니다."),
    FAILED_TO_INSERT_PLACES(HttpStatus.INTERNAL_SERVER_ERROR, "장소(place) 데이터를 삽입하는 데 실패했습니다."),
    FAILED_TO_INSERT_VIDEOS(HttpStatus.INTERNAL_SERVER_ERROR, "비디오(video) 데이터를 삽입하는 데 실패했습니다."),
    FAILED_TO_INSERT_TRAVEL_COURSES(HttpStatus.INTERNAL_SERVER_ERROR, "여행 코스(travel_courses) 데이터를 삽입하는 데 실패했습니다."),
    FAILED_TO_INSERT_TRAVEL_COURSE_DETAILS(HttpStatus.INTERNAL_SERVER_ERROR, "여행 코스 상세정보(travel_course_details)를 삽입하는 데 실패했습니다."),

    GOOGLE_API_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Google API 동기화 중 오류가 발생했습니다."),
    SHEET_DATA_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "시트 데이터 저장 중 DB 오류가 발생했습니다."),
    DATA_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "시트 데이터 동기화에 실패했습니다."),

    FAILED_TO_UPDATE_LAST_PROCESSED_ROW(HttpStatus.INTERNAL_SERVER_ERROR, "테이블의 마지막 처리된 행 정보를 업데이트하는 데 실패했습니다."),
    BATCH_INSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "배치 삽입에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorMessage(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
