package kr.hhplus.be.commerce.interfaces;

import kr.hhplus.be.commerce.domain.coupon.*;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.interfaces.dto.request.CouponIssueRequest;
import kr.hhplus.be.commerce.utils.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestCoupon;
import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("사용자 ID가 null일 경우 유효성 검사 실패")
    void issueCoupon_ShouldFailValidation_WhenUserIdIsNull() throws Exception {
        // given
        CouponIssueRequest request = new CouponIssueRequest(null, 1L);  // userId가 null

        // when & then
        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400 오류
                .andExpect(jsonPath("$.errors[0].message").value("사용자 ID는 필수값입니다."));
    }

    @Test
    @DisplayName("사용자 ID가 음수일 경우 유효성 검사 실패")
    void issueCoupon_ShouldFailValidation_WhenUserIdIsNegative() throws Exception {
        // given
        CouponIssueRequest request = new CouponIssueRequest(-1L, 1L);  // userId가 음수

        // when & then
        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400 오류
                .andExpect(jsonPath("$.errors[0].message").value("유효하지 않은 사용자 ID입니다."));
    }

    @Test
    @DisplayName("쿠폰 ID가 null일 경우 유효성 검사 실패")
    void issueCoupon_ShouldFailValidation_WhenCouponIdIsNull() throws Exception {
        // given
        CouponIssueRequest request = new CouponIssueRequest(1L, null);  // couponId가 null

        // when & then
        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400 오류
                .andExpect(jsonPath("$.errors[0].message").value("쿠폰 ID는 필수값입니다."));
    }

    @Test
    @DisplayName("쿠폰 ID가 음수일 경우 유효성 검사 실패")
    void issueCoupon_ShouldFailValidation_WhenCouponIdIsNegative() throws Exception {
        // given
        CouponIssueRequest request = new CouponIssueRequest(1L, -1L);  // couponId가 음수

        // when & then
        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400 오류
                .andExpect(jsonPath("$.errors[0].message").value("유효하지 않은 쿠폰 ID입니다."));
    }

    @Test
    @DisplayName("정상적인 사용자 ID와 쿠폰 ID로 쿠폰 발급 성공")
    void issueCoupon_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        // given
        User user = userJpaRepository.save(createTestUser());

        Coupon coupon = couponJpaRepository.save(Coupon.create(
                "COUPON_1", "쿠폰 1", "설명 1", DiscountType.PERCENTAGE,
                10L, 10000L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 100L
        ));

        CouponQuantity couponQuantity = CouponQuantity.create(coupon.getCouponId(), 10L);
        couponQuantityJpaRepository.save(couponQuantity);
        CouponIssueRequest request = new CouponIssueRequest(user.getUserId(), coupon.getCouponId());

        // when & then
        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.message").value("쿠폰 발급이 완료되었습니다."));
    }

    @Test
    @DisplayName("정상적인 사용자 ID로 쿠폰 목록 조회 성공")
    void getCouponList_ShouldReturnCoupons_WhenValidUserId() throws Exception {
        // given
        User user = userJpaRepository.save(createTestUser());
        int couponCount = 5;
        List<Coupon> couponList = new ArrayList<>();
        for (int i = 0; i < couponCount; i++) {
            Coupon coupon = couponJpaRepository.save(createTestCoupon(
                    (long) i + 1,
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now().plusDays(7),
                    10L
            ));
            userCouponJpaRepository.save(UserCoupon.create(
                    user.getUserId(),
                    coupon.getCouponId(),
                    UserCouponStatus.ISSUED,
                    LocalDateTime.now()
            ));
            couponList.add(coupon);
        }

        // when & then
        mockMvc.perform(get("/api/v1/coupons/{userId}/list", user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("쿠폰 목록 조회 성공"))
                .andExpect(jsonPath("$.data.length()").value(couponCount));
    }
}
