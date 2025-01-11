package kr.hhplus.be.commerce.domain.order;


import lombok.RequiredArgsConstructor;

/**
 * 주문 상태
 */
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("PENDING", "결제 대기"),
    PAID("PAID", "결제 완료"),
    FAILED("FAILED", "결제 실패"),
    CANCELED("CANCELED", "취소");
    private final String type;
    private final String name;

}