package org.greedy.ddarahang.common.exception;

public enum ErrorMessage {

    MISSING_ID_EXCEPTION("ID가 누락되었습니다."),
    INVALID_COUNTRY_NAME_EXCEPTION("유효하지 않은 국가명입니다."),
    INVALID_DATE_FORMAT_EXCEPTION("날짜 형식이 잘못되었습니다."),
    INVALID_FILTER_EXCEPTION("유효하지 않은 필터입니다."),
    NOT_FOUND_COUNTRY_EXCEPTION("국가 정보를 찾을 수 없습니다."),
    NOT_FOUND_REGION_EXCEPTION("지역 정보를 찾을 수 없습니다"),
    NOT_FOUND_TRAVELCOURSEDETAIL_EXCEPTION("여행 코스 상세정보를 찾을 수 없습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
