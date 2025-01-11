package kr.hhplus.be.commerce.infra.payment;

import kr.hhplus.be.commerce.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
