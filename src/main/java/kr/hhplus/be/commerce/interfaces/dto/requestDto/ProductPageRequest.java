package kr.hhplus.be.commerce.interfaces.dto.requestDto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record ProductPageRequest(
        @PositiveOrZero(message = "유효하지 않은 페이지 크기입니다.")
        Integer size,
        @Positive(message = "유효하지 않은 페이지 번호입니다.")
        Integer page
) {
}
