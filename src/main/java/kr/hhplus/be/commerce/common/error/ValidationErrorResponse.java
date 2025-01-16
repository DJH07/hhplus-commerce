package kr.hhplus.be.commerce.common.error;

import java.util.List;

public record ValidationErrorResponse(
        String detail,
        List<ErrorResponse> errors
) {
}
