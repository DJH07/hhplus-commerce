package kr.hhplus.be.commerce.app.dto;

import lombok.Builder;

@Builder
public record OrderProductItemResult(
        Long productId,
        Long quantity,
        Long totalPrice
) {
}
