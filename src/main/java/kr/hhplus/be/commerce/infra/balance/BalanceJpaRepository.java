package kr.hhplus.be.commerce.infra.balance;

import kr.hhplus.be.commerce.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByUserId(Long userId);
}
