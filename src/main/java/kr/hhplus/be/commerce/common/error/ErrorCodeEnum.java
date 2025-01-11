package kr.hhplus.be.commerce.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCodeEnum {
    HttpStatus getStatus();
    String getMsg();
}

