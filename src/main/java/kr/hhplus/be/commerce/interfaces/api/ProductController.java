package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.ProductPageRequest;
import kr.hhplus.be.commerce.app.dto.ProductResponse;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import kr.hhplus.be.commerce.app.dto.TopProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    // TODO: 상품 목록 조회 API
    @Operation(summary = "상품 목록 조회", description = "상품 정보 목록을 조회하는 API", tags = {"상품"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping("/list")
    public ResponseEntity<ResponseDto> list(@Valid @ModelAttribute ProductPageRequest request) {
        List<ProductResponse> products = List.of(
                new ProductResponse(1L, "상품1", 5000L, "상품 설명", 100L),
                new ProductResponse(2L, "상품2", 3000L, "상품 설명 2", 200L)
        );
        Map<String, Object> data = Map.of(
                "data", products,
                "totalCount", 100
        );

        ResponseDto responseDto = ResponseDto.builder()
                .message("상품 목록 조회 성공")
                .data(data)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // TODO: 상위 주문 상품 조회 API
    @Operation(summary = "상위 주문 상품 목록 조회", description = "근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공하는 API", tags = {"상품"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping("/top")
    public ResponseEntity<ResponseDto> top() {
        List<TopProductResponse> products = List.of(
                new TopProductResponse(1L, 1, "상품1", 5000L, "상품 설명", 150L),
                new TopProductResponse(2L, 2, "상품2", 3000L, "상품 설명 2", 120L),
                new TopProductResponse(3L, 3, "상품3", 2000L, "상품 설명 3", 100L),
                new TopProductResponse(4L, 4,  "상품4", 7000L, "상품 설명 4", 80L),
                new TopProductResponse(5L, 5, "상품5", 4000L, "상품 설명 5", 70L)
        );

        ResponseDto responseDto = ResponseDto.builder()
                .message("상위 상품 목록 조회 성공")
                .data(products)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
