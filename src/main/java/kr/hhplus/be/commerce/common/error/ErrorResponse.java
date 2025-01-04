package kr.hhplus.be.commerce.common.error;

public record ErrorResponse(
        String code,
        String message
) {
}
