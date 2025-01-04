package kr.hhplus.be.commerce.infra.balance;

import kr.hhplus.be.commerce.domain.balance.Balance;
import kr.hhplus.be.commerce.domain.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BalanceRepositoryImpl implements BalanceRepository {

    private final BalanceJpaRepository balanceJpaRepository;

    @Override
    public Balance findByUserId(Long userId) {
        return balanceJpaRepository.findByUserId(userId)
                .orElseGet(() -> balanceJpaRepository.save(Balance.create(userId, 0L)));
    }
}
