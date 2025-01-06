package kr.hhplus.be.commerce.domain.order;

public record RegisterOrderCommand(Long userId,
                                   Long orderTotalAmount,
                                   Long payTotalAmount) {
}
