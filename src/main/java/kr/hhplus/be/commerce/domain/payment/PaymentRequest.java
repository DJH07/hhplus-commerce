package kr.hhplus.be.commerce.domain.payment;

public record PaymentRequest(
        Long orderId,
        Long amount,
        boolean isFail
) {
}
