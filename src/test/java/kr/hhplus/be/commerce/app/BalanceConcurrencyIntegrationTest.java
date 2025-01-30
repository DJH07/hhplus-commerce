package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.balance.Balance;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;

class BalanceConcurrencyIntegrationTest extends IntegrationTest {
    @Autowired
    private BalanceFacade balanceFacade;

    @Test
    @DisplayName("잔액 충전 더블클릭 테스트")
    void charge_ConcurrentRequests_ShouldHandleCorrectly() {
        // given
        User user = createTestUser();
        userJpaRepository.save(user);

        long initialBalance = 2000L;
        Balance balance = Balance.create(user.getUserId(), initialBalance);
        balanceJpaRepository.save(balance);

        long userId = user.getUserId();
        long chargeAmount = 500L;

        // when
        Runnable task = () -> balanceFacade.charge(userId, chargeAmount);

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // then
        Balance updatedBalance = balanceRepository.findByUserId(userId);
        Assertions.assertNotNull(updatedBalance);
        Assertions.assertEquals(initialBalance + chargeAmount, updatedBalance.getAmount());
    }

}
