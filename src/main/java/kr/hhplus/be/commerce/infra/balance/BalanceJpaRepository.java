package kr.hhplus.be.commerce.infra.balance;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
}
