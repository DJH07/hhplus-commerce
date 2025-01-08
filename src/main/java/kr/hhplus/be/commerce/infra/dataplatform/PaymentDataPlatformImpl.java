package kr.hhplus.be.commerce.infra.dataplatform;

import kr.hhplus.be.commerce.domain.payment.PaymentDataPlatform;
import kr.hhplus.be.commerce.domain.payment.PaymentRequest;
import kr.hhplus.be.commerce.domain.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDataPlatformImpl implements PaymentDataPlatform {
    private final PaymentDataPlatformClient client;

    @Override
    public PaymentResponse sendPaymentData(PaymentRequest paymentRequest) {
        return client.sendPaymentData(paymentRequest);
    }
}
