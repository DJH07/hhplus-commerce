package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.app.ProductFacade;
import kr.hhplus.be.commerce.app.dto.ProductResponse;
import kr.hhplus.be.commerce.app.dto.TopProductResponse;
import kr.hhplus.be.commerce.interfaces.dto.request.ProductPageRequest;
import kr.hhplus.be.commerce.interfaces.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @Operation(summary = "상품 목록 조회", description = "상품 정보 목록을 조회하는 API", tags = {"상품"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping("/list")
    public ResponseEntity<ResponseDto> list(@Valid @ModelAttribute ProductPageRequest request) {

        Page<ProductResponse> products = productFacade.getProductPage(request.size(), request.page());

        ResponseDto responseDto = ResponseDto.builder()
                .message("상품 목록 조회 성공")
                .data(products)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "상위 주문 상품 목록 조회", description = "근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공하는 API", tags = {"상품"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping("/top")
    public ResponseEntity<ResponseDto> top() {
        List<TopProductResponse> products = productFacade.getTopProductList();

        ResponseDto responseDto = ResponseDto.builder()
                .message("상위 상품 목록 조회 성공")
                .data(products)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
