package kr.hhplus.be.commerce.domain;

import kr.hhplus.be.commerce.domain.balance.Balance;
import kr.hhplus.be.commerce.domain.balance.BalanceRepository;
import kr.hhplus.be.commerce.domain.balance.BalanceService;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceUnitTest {
    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    @DisplayName("잔액 충전 시 잔고 허용치를 넘길 경우 BALANCE_EXCEEDS_LIMIT 발생")
    void chargeBalance_ShouldThrowException_WhenBalanceExceedsLimit() {
        // given
        final long userId = 1;
        final long amount = 1;
        final Balance balance = Balance.create(userId, Long.MAX_VALUE);

        when(balanceRepository.findByUserId(userId))
                .thenReturn(balance);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> balanceService.chargeBalance(userId, amount)
        );

        // then
        assertEquals(BusinessErrorCode.BALANCE_EXCEEDS_LIMIT, exception.getErrorCode());
    }

    @Test
    @DisplayName("잔액 차감 시 잔고가 부족한 경우 INSUFFICIENT_BALANCE 발생")
    void deductBalance_ShouldThrowException_WhenInsufficientBalance() {
        // given
        final long userId = 1;
        final long amount = 1000;
        final Balance balance = Balance.create(userId, 500L);

        when(balanceRepository.findByUserId(userId))
                .thenReturn(balance);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> balanceService.deductBalance(userId, amount)
        );

        // then
        assertEquals(BusinessErrorCode.INSUFFICIENT_BALANCE, exception.getErrorCode());
    }


}
