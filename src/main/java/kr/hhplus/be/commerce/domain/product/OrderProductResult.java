package kr.hhplus.be.commerce.domain.product;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderProductResult(
        Long totalAmount,
        List<OrderProductItemResult> itemResultList
) {
}
