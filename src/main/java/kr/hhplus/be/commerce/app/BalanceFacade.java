package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.balance.BalanceService;
import kr.hhplus.be.commerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceFacade {

    private final BalanceService balanceService;
    private final UserService userService;

    public Long charge(Long userId, Long amount) {

        userService.checkUserExists(userId);

        return balanceService.chargeBalance(userId, amount);

    }

    public Long getBalanceAmount(Long userId) {

        userService.checkUserExists(userId);

        return balanceService.getBalanceAmount(userId);

    }
}
