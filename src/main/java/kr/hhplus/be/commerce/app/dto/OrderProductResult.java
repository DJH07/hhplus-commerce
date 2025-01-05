package kr.hhplus.be.commerce.app.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderProductResult(
        Long totalAmount,
        List<OrderProductItemResult> itemResultList
) {
}
