package kr.hhplus.be.commerce.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentDataPlatform paymentDataPlatform;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentStatus processPayment(Long orderId, Long amount) {
        PaymentRequest request = new PaymentRequest(orderId, amount);
        PaymentResponse response = paymentDataPlatform.sendPaymentData(request);

        registerPayment(request, response);

        return response.status();
    }

    protected void registerPayment(PaymentRequest request, PaymentResponse response) {
        paymentRepository.save(Payment.create(
                request.orderId(),
                request.amount(),
                response.status(),
                LocalDateTime.now(),
                response.failureReason()));
    }

}
