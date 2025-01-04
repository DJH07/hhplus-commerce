package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.ChargeBalanceRequest;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.UserBalanceRequest;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
@Validated
public class BalanceController {

    // TODO: 잔액 충전 API
    @Operation(summary = "잔액 충전", description = "결제에 사용될 금액을 충전하는 API", tags = {"잔액"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @PatchMapping("/charge")
    public ResponseEntity<?> charge(@Valid @RequestBody ChargeBalanceRequest request) {
        ResponseDto responseDto = ResponseDto.builder()
                .message("충전이 완료되었습니다.")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // TODO: 잔액 조회 API
    @Operation(summary = "잔액 조회", description = "사용자 식별자를 통해 해당 사용자의 잔액을 조회하는 API", tags = {"잔액"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
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
