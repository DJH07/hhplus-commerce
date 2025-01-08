package kr.hhplus.be.commerce.domain.order;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderResult(
        Long payTotalAmount,
        Long userId,
        List<OrderItemResult> itemResultList
) {
}
