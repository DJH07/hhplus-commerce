package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.balance.Balance;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;

class BalanceFacadeIntegrationTest extends IntegrationTest {
    @Autowired
    private BalanceFacade balanceFacade;

    @Test
    @DisplayName("잔액 충전 성공 시 잔고가 업데이트")
    void charge_ShouldUpdateBalance_WhenSuccessful() {
        // given
        User user = createTestUser();
        userJpaRepository.save(user);

        long initialBalance = 2000L;
        Balance balance = Balance.create(user.getUserId(), initialBalance);
        balanceJpaRepository.save(balance);

        long userId = user.getUserId();
        long chargeAmount = 5000L;

        // when
        balanceFacade.charge(userId, chargeAmount);

        // then
        Balance updatedBalance = balanceRepository.findByUserId(userId);
        Assertions.assertNotNull(updatedBalance);
        Assertions.assertEquals(initialBalance + chargeAmount, updatedBalance.getAmount());
    }

    @Test
    @DisplayName("잔액 조회 성공 시, 잔액이 올바르게 반환")
    void getBalanceAmount_ShouldReturnCorrectBalance_WhenUserExists() {
        // given
        User user = createTestUser();
        userJpaRepository.save(user);

        long initialBalance = 2000L;
        Balance balance = Balance.create(user.getUserId(), initialBalance);
        balanceJpaRepository.save(balance);

        long userId = user.getUserId();

        // when
        long balanceAmount = balanceFacade.getBalanceAmount(userId);

        // then
        Assertions.assertEquals(initialBalance, balanceAmount);
    }
}
