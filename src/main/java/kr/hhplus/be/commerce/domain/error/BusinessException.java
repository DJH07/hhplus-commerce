package kr.hhplus.be.commerce.domain.error;

import kr.hhplus.be.commerce.common.error.ErrorCodeEnum;
import lombok.Getter;

/*
 * 비즈니스 에러 handler
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCodeEnum errorCode;

    public BusinessException(ErrorCodeEnum errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
