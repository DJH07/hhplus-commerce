package kr.hhplus.be.commerce.interfaces.api;

import jakarta.validation.Valid;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.CouponIssueRequest;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.CouponResponse;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {

    // TODO: 선착순 쿠폰 발급 API
    @PostMapping("/issue")
    public ResponseEntity<?> issueCoupon(@Valid @RequestBody CouponIssueRequest request) {
        boolean isIssued = request.couponId() == 101 && request.userId() == 1; // 임의 로직

        if (isIssued) {
            ResponseDto responseDto = ResponseDto.builder()
                    .message("쿠폰 발급이 완료되었습니다.")
                    .build();

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "쿠폰 발급에 실패했습니다. 선착순 인원이 초과되었습니다."));
        }
    }

    // TODO: 선착순 쿠폰 목록 조회 API
    @GetMapping("/{userId}/list")
    public ResponseEntity<?> getCouponList(@PathVariable Long userId) {
        if (userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "사용자 ID가 유효하지 않습니다."));
        }

        List<CouponResponse> coupons = List.of(
                new CouponResponse(
                        101L,
                        "할인 쿠폰",
                        "10% 할인",
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 1, 31),
                        "발급 완료"
                )
        );

        ResponseDto responseDto = ResponseDto.builder()
                .message("쿠폰 목록 조회 성공")
                .data(coupons)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}