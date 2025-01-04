package kr.hhplus.be.commerce.domain.balance;

public interface BalanceRepository {

    Balance findByUserId(Long userId);

}
