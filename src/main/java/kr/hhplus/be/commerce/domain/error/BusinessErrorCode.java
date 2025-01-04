package kr.hhplus.be.commerce.domain.error;


import kr.hhplus.be.commerce.common.error.ErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 오류 코드 종류
 */
@RequiredArgsConstructor
public enum BusinessErrorCode implements ErrorCodeEnum {
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "사용자 ID가 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 사용자를 찾을 수 없습니다."),
    BALANCE_EXCEEDS_LIMIT(HttpStatus.BAD_REQUEST, "잔액이 허용된 최대 한도를 초과합니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    LOCK_TIMEOUT(HttpStatus.BAD_REQUEST, "현재 처리 중인 요청이 많아 신청이 지연되고 있습니다.");
    private final HttpStatus status;
    private final String msg;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}