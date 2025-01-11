package kr.hhplus.be.commerce.domain.balance;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

    @Transactional
    public Long chargeBalance(Long userId, Long amount) {
        Balance balance = balanceRepository.findByUserId(userId);

        if(balance.getAmount() > Long.MAX_VALUE - amount) {
            throw new BusinessException(BusinessErrorCode.BALANCE_EXCEEDS_LIMIT);
        }

        balance.changeAmount(balance.getAmount() + amount);

        return balance.getBalanceId();
    }

    public void reduceBalance(Long userId, Long amount) {
        Balance balance = balanceRepository.findByUserId(userId);

        if(balance.getAmount() < amount) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_BALANCE);
        }

        balance.changeAmount(balance.getAmount() - amount);
    }

    public Long getBalanceAmount(Long userId) {
        return balanceRepository.findByUserId(userId).getAmount();
    }
}
