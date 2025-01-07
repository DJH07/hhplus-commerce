package kr.hhplus.be.commerce.domain.error;


import kr.hhplus.be.commerce.common.error.ErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 오류 코드 종류
 */
@RequiredArgsConstructor
public enum BusinessErrorCode implements ErrorCodeEnum {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 사용자를 찾을 수 없습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 쿠폰을 찾을 수 없습니다."),
    NOT_FOUND_COUPON_QUANTITY(HttpStatus.NOT_FOUND, "해당 쿠폰의 개수를 찾을 수 없습니다."),
    USER_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 사용자 보유 쿠폰을 찾을 수 없습니다."),
    BALANCE_EXCEEDS_LIMIT(HttpStatus.BAD_REQUEST, "잔액이 허용된 최대 한도를 초과합니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용한 쿠폰입니다."),
    COUPON_MIN_ORDER_AMOUNT_NOT_MET(HttpStatus.BAD_REQUEST, "쿠폰 사용을 위한 최소 주문 금액을 충족하지 못했습니다."),
    OUT_OF_COUPONS(HttpStatus.BAD_REQUEST, "모든 쿠폰이 소진되었습니다."),
    COUPON_NOT_YET_ACTIVE(HttpStatus.BAD_REQUEST, "쿠폰이 아직 활성화되지 않았습니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "사용 기간이 만료된 쿠폰입니다."),
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