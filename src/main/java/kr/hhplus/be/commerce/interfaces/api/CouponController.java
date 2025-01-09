package kr.hhplus.be.commerce.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.be.commerce.app.CouponFacade;
import kr.hhplus.be.commerce.app.dto.CouponResponse;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.CouponIssueRequest;
import kr.hhplus.be.commerce.interfaces.dto.responseDto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    @Operation(summary = "선착순 쿠폰 발급", description = "선착순 쿠폰 발급하는 API", tags = {"쿠폰"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @PostMapping("/issue")
    public ResponseEntity<?> issueCoupon(@Valid @RequestBody CouponIssueRequest request) {

        Long response = couponFacade.issueCoupon(request.userId(), request.couponId());
        ResponseDto responseDto = ResponseDto.builder()
                .message("쿠폰 발급이 완료되었습니다.")
                .data(response)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "선착순 쿠폰 목록 조회", description = "보유 쿠폰 정보 목록을 조회하는 API", tags = {"쿠폰"})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CouponResponse.class)))
    @GetMapping("/{userId}/list")
    public ResponseEntity<?> getCouponList(@PathVariable Long userId) {
        if (userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "사용자 ID가 유효하지 않습니다."));
        }

        List<CouponResponse> coupons = couponFacade.getUserCouponList(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .message("쿠폰 목록 조회 성공")
                .data(coupons)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
