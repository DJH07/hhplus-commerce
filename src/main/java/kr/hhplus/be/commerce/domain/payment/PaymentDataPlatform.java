package kr.hhplus.be.commerce.domain.payment;

public interface PaymentDataPlatform {

    PaymentResponse sendPaymentData(PaymentRequest paymentRequest);

}
