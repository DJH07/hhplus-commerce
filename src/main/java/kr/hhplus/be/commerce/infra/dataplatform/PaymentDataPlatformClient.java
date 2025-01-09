package kr.hhplus.be.commerce.infra.dataplatform;

import kr.hhplus.be.commerce.domain.payment.PaymentRequest;
import kr.hhplus.be.commerce.domain.payment.PaymentResponse;
import kr.hhplus.be.commerce.domain.payment.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataPlatformClient {
    public PaymentResponse sendPaymentData(PaymentRequest paymentRequest) {
        // 결제 요청을 전송하고 응답을 받는 시뮬레이션

        // FIXME : 임의의 처리(특히 1000000보다 큰 경우 실패)이므로 추후 수정
        if (paymentRequest.orderId() == -1 || paymentRequest.amount() == -1 || paymentRequest.amount() > 1000000) {
            // 결제 실패 처리
            return new PaymentResponse(PaymentStatus.FAILED, "잘못된 주문 ID 또는 금액");
        }

        // 결제 성공 처리
        return new PaymentResponse(PaymentStatus.SUCCESS, "");
    }
}
