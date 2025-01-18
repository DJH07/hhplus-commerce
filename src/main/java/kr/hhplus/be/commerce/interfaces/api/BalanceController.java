package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.app.BalanceFacade;
import kr.hhplus.be.commerce.interfaces.dto.request.ChargeBalanceRequest;
import kr.hhplus.be.commerce.interfaces.dto.request.UserBalanceRequest;
import kr.hhplus.be.commerce.interfaces.dto.response.ResponseDto;
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

    private final BalanceFacade balanceFacade;

    @Operation(summary = "잔액 충전", description = "결제에 사용될 금액을 충전하는 API", tags = {"잔액"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @PatchMapping("/charge")
    public ResponseEntity<?> charge(@Valid @RequestBody ChargeBalanceRequest request) {

        Long response = balanceFacade.charge(request.userId(), request.amount());

        ResponseDto responseDto = ResponseDto.builder()
                .message("충전이 완료되었습니다.")
                .data(response)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "잔액 조회", description = "사용자 식별자를 통해 해당 사용자의 잔액을 조회하는 API", tags = {"잔액"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@Valid @ModelAttribute UserBalanceRequest request) {

        Long response = balanceFacade.getBalanceAmount(request.userId());

        ResponseDto responseDto = ResponseDto.builder()
                .message("잔액 조회 성공")
                .data(response)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
