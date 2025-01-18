package kr.hhplus.be.commerce.domain.payment;


import lombok.RequiredArgsConstructor;

/**
 * 결제 상태
 */
@RequiredArgsConstructor
public enum PaymentStatus {
    SUCCESS("SUCCESS", "결제 성공"),
    FAILED("FAILED", "결제 실패");
    private final String type;
    private final String name;

}