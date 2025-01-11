package kr.hhplus.be.commerce.domain.coupon;


import lombok.RequiredArgsConstructor;

/**
 * 할인 종류
 */
@RequiredArgsConstructor
public enum DiscountType {
    PERCENTAGE("PERCENTAGE", "비율 할인"),
    AMOUNT("AMOUNT", "정액 할인");
    private final String type;
    private final String name;

}