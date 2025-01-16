package kr.hhplus.be.commerce.interfaces;

import kr.hhplus.be.commerce.domain.balance.Balance;
import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.UserCoupon;
import kr.hhplus.be.commerce.domain.coupon.UserCouponStatus;
import kr.hhplus.be.commerce.domain.order.Order;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.interfaces.dto.request.PaymentRequest;
import kr.hhplus.be.commerce.utils.TestUtils;
import kr.hhplus.be.commerce.utils.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("결제 요청 성공 통합 테스트")
    void processPayment_Success() throws Exception {
        // Given
        User user = userJpaRepository.save(TestUtils.createTestUser());
        balanceJpaRepository.save(Balance.create(user.getUserId(), 1000000L));
        Order order = orderJpaRepository.save(Order.create(user.getUserId(), 500L, LocalDateTime.now()));
        Coupon coupon = couponJpaRepository.save(TestUtils.createTestCoupon(1L,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(7), 10L));
        UserCoupon userCoupon = userCouponJpaRepository.save(UserCoupon.create(user.getUserId(), coupon.getCouponId(), UserCouponStatus.ISSUED, LocalDateTime.now()));

        PaymentRequest paymentRequest = new PaymentRequest(order.getOrderId(), userCoupon.getUserCouponId());

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/payments/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("결제가 완료되었습니다."))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }

    @Test
    @DisplayName("결제 요청 실패 유효성 검증 실패")
    void processPayment_ValidationFail() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(null, -1L); // orderId가 null, userCouponId가 음수

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/payments/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.code == 'orderId')].message").value("주문 ID는 필수값입니다."));

    }

}
