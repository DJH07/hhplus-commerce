package kr.hhplus.be.commerce.infra.payment;

import kr.hhplus.be.commerce.domain.payment.Payment;
import kr.hhplus.be.commerce.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Long save(Payment payment) {
        return paymentJpaRepository.save(payment).getPaymentId();
    }
}
