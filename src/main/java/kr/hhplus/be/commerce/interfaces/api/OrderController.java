package kr.hhplus.be.commerce.interfaces.api;

import jakarta.validation.Valid;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.OrderRequest;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// TODO: 주문 API
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    // 주문 API
    @PostMapping
    public ResponseEntity<ResponseDto> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        // Mock 응답
        ResponseDto responseDto = ResponseDto.builder()
                .message("주문이 성공적으로 처리되었습니다.")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 결제 API
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<ResponseDto> processPayment(@PathVariable Long orderId) {
        // Mock 응답
        ResponseDto responseDto = ResponseDto.builder()
                .message("결제가 완료되었습니다.")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
