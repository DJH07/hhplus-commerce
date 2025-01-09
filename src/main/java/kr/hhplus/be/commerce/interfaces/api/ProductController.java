package kr.hhplus.be.commerce.interfaces.api;

import jakarta.validation.Valid;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.ProductPageRequest;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ProductResponse;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.TopProductResponse;
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
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    // TODO: 상품 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<ResponseDto> list(@Valid @ModelAttribute ProductPageRequest request) {
        List<ProductResponse> products = List.of(
                new ProductResponse(1L, "상품1", 5000, "상품 설명", 100),
                new ProductResponse(2L, "상품2", 3000, "상품 설명 2", 200)
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
    @GetMapping("/top")
    public ResponseEntity<ResponseDto> top() {
        List<TopProductResponse> products = List.of(
                new TopProductResponse(1L, "상품1", 5000, "상품 설명", 150),
                new TopProductResponse(2L, "상품2", 3000, "상품 설명 2", 120),
                new TopProductResponse(3L, "상품3", 2000, "상품 설명 3", 100),
                new TopProductResponse(4L, "상품4", 7000, "상품 설명 4", 80),
                new TopProductResponse(5L, "상품5", 4000, "상품 설명 5", 70)
        );

        ResponseDto responseDto = ResponseDto.builder()
                .message("상위 상품 목록 조회 성공")
                .data(products)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
