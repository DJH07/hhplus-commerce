package kr.hhplus.be.commerce.app.dto;

public record RegisterOrderCommand(Long userId,
                                   Long orderTotalAmount,
                                   Long payTotalAmount) {
}
