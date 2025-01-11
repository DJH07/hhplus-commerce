package kr.hhplus.be.commerce.domain.coupon;


import lombok.RequiredArgsConstructor;

/**
 * 할인 종류
 */
@RequiredArgsConstructor
public enum UserCouponStatus {
    ISSUED("ISSUED", "발급"),
    USED("USED", "사용");
    private final String type;
    private final String name;

}