package kr.hhplus.be.commerce.interfaces.api;

import jakarta.validation.Valid;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.ChargeBalanceRequest;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.UserBalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
@Validated
public class BalanceController {

    // TODO: 잔액 충전 API
    @PatchMapping("/charge")
    public ResponseEntity<?> charge(@Valid @RequestBody ChargeBalanceRequest request) {
        ResponseDto responseDto = ResponseDto.builder()
                .message("충전이 완료되었습니다.")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // TODO: 잔액 조회 API
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@Valid @ModelAttribute UserBalanceRequest request) {
        System.out.println("request = " + request.toString());
        ResponseDto responseDto = ResponseDto.builder()
                .message("잔액 조회 성공")
                .data(5000)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
