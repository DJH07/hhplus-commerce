package kr.hhplus.be.commerce.interfaces.dto;

import lombok.Builder;

@Builder
public record ResponseDto(
        Integer code,
        String message,
        Object data
) {
}
