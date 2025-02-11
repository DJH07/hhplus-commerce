package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.app.OrderFacade;
import kr.hhplus.be.commerce.interfaces.dto.request.OrderRequest;
import kr.hhplus.be.commerce.interfaces.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @Operation(summary = "주문", description = "사용자 식별자와 상품 정보 목록을 입력받아 주문하는 API", tags = {"주문"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @PostMapping
    public ResponseEntity<ResponseDto> createOrder(@Valid @RequestBody OrderRequest request) {

        Long response = orderFacade.order(request.userId(), request.items());

        ResponseDto responseDto = ResponseDto.builder()
                .message("주문이 성공적으로 처리되었습니다.")
                .data(response)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
