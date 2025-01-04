package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.OrderRequest;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// TODO: 주문 API
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    // 주문 API
    @Operation(summary = "주문", description = "사용자 식별자와 상품 정보 목록을 입력받아 주문하는 API", tags = {"주문"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @PostMapping
    public ResponseEntity<ResponseDto> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        // Mock 응답
        ResponseDto responseDto = ResponseDto.builder()
                .message("주문이 성공적으로 처리되었습니다.")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 결제 API
    @Operation(summary = "결제", description = "주문 정보를 입력 받아 주문하고 결제를 수행하는 API", tags = {"주문"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<ResponseDto> processPayment(@PathVariable Long orderId) {
        // Mock 응답
        ResponseDto responseDto = ResponseDto.builder()
                .message("결제가 완료되었습니다.")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
