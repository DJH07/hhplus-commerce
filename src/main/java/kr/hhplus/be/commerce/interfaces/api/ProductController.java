package kr.hhplus.be.commerce.interfaces.api;

import kr.hhplus.be.commerce.interfaces.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    // TODO: 상품 조회 API
    @GetMapping("/list")
    public ResponseEntity<ResponseDto> getProductList(
            @RequestParam int page,
            @RequestParam int size) {
        ResponseDto responseDto = ResponseDto.builder()
                .message("상품 목록 조회 성공")
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // TODO: 상위 주문 상품 조회 API
}
