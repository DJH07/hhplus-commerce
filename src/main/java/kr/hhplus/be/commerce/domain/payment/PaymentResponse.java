package kr.hhplus.be.commerce.domain.payment;

public record PaymentResponse(
        PaymentStatus status,
        String failureReason
) {
}
