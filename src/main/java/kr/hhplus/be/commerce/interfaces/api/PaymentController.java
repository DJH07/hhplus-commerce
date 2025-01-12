package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.app.PaymentFacade;
import kr.hhplus.be.commerce.domain.payment.PaymentStatus;
import kr.hhplus.be.commerce.interfaces.dto.request.PaymentRequest;
import kr.hhplus.be.commerce.interfaces.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;


    @Operation(summary = "결제", description = "주문 정보를 입력 받아 주문하고 결제를 수행하는 API", tags = {"주문"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @PostMapping("/payment")
    public ResponseEntity<ResponseDto> processPayment(@Valid @RequestBody PaymentRequest request) {

        PaymentStatus response = paymentFacade.payment(request.orderId(), request.userCouponId(), false);

        ResponseDto responseDto = ResponseDto.builder()
                .message("결제가 완료되었습니다.")
                .data(response)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
