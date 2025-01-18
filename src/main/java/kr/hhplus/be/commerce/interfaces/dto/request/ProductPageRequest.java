package kr.hhplus.be.commerce.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record ProductPageRequest(
        @NotNull(message = "페이지 크기는 필수값입니다.")
        @Positive(message = "유효하지 않은 페이지 크기입니다.")
        @Schema(description = "페이지 크기")
        Integer size,
        @NotNull(message = "페이지 번호는 필수값입니다.")
        @PositiveOrZero(message = "유효하지 않은 페이지 번호입니다.")
        @Schema(description = "페이지 번호")
        Integer page
) {
}
