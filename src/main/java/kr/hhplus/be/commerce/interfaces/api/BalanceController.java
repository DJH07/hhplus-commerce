package kr.hhplus.be.commerce.interfaces.api;

import kr.hhplus.be.commerce.interfaces.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {

    // TODO: 잔액 충전 API
    @PatchMapping("/charge")
    public ResponseEntity<?> charge() {
        ResponseDto responseDto = ResponseDto.builder()
                .message("충전이 완료되었습니다.")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // TODO: 잔액 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        ResponseDto responseDto = ResponseDto.builder()
                .message("잔액 조회 성공")
                .data(5000)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
