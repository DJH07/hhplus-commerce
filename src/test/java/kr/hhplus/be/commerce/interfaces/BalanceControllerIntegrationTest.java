package kr.hhplus.be.commerce.interfaces;

import kr.hhplus.be.commerce.domain.balance.Balance;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.interfaces.dto.request.ChargeBalanceRequest;
import kr.hhplus.be.commerce.utils.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BalanceControllerIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("잔액 충전 요청에서 userId가 null일 경우 validation 에러 발생")
    void chargeBalance_ShouldFailValidation_WhenUserIdIsNull() throws Exception {
        // given
        ChargeBalanceRequest request = ChargeBalanceRequest.builder()
                .userId(null)
                .amount(100L)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/balances/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.errors[0].code").value("userId"))
                .andExpect(jsonPath("$.errors[0].message").value("사용자 ID는 필수값입니다."));
    }

    @Test
    @DisplayName("잔액 충전 요청에서 amount가 0 이하일 경우 validation 에러 발생")
    void chargeBalance_ShouldFailValidation_WhenAmountIsInvalid() throws Exception {
        // given
        User user = userJpaRepository.save(createTestUser());

        ChargeBalanceRequest request = ChargeBalanceRequest.builder()
                .userId(user.getUserId())
                .amount(-100L)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/balances/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.errors[0].message").value("충전 금액이 유효하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 충전 요청 성공 통합 테스트")
    void chargeBalance_ShouldSucceed() throws Exception {
        // given
        User user = userJpaRepository.save(createTestUser());
        balanceJpaRepository.save(Balance.create(user.getUserId(), 1000L));

        ChargeBalanceRequest request = ChargeBalanceRequest.builder()
                .userId(user.getUserId())
                .amount(500L)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/balances/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("충전이 완료되었습니다."));
    }

    @Test
    @DisplayName("잔액 조회 성공")
    void getBalance_Success() throws Exception {
        // Given
        User user = userJpaRepository.save(createTestUser());
        Long balanceAmount = 5000L;
        balanceJpaRepository.save(Balance.create(user.getUserId(), balanceAmount));

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/balances/balance")
                .param("userId", String.valueOf(user.getUserId()))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("잔액 조회 성공"))
                .andExpect(jsonPath("$.data").value(balanceAmount));
    }

    @Test
    @DisplayName("잔액 조회 실패 - 사용자 ID가 null")
    void getBalance_ShouldFailValidation_WhenUserIdIsNull() throws Exception {
        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/balances/balance")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.errors[0].code").value("userId"))
                .andExpect(jsonPath("$.errors[0].message").value("사용자 ID는 필수값입니다."));
    }

    @Test
    @DisplayName("잔액 조회 실패 - 사용자 ID가 음수")
    void getBalance_ShouldFailValidation_WhenUserIdIsNegative() throws Exception {
        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/balances/balance")
                .param("userId", "-1")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.errors[0].code").value("userId"))
                .andExpect(jsonPath("$.errors[0].message").value("유효하지 않은 사용자 ID입니다."));
    }

}
