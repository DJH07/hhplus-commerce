package kr.hhplus.be.commerce.interfaces.dto.responseDto;

import lombok.Builder;

@Builder
public record ResponseDto(
        Integer code,
        String message,
        Object data
) {
}
