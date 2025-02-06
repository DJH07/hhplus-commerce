package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponScheduler {
    private final CouponService couponService;

    @Scheduled(fixedDelay = 30 * 1000)
    public void processCouponQueue() {
        List<Long> couponIds = couponService.getAllCouponIds();

        for (Long couponId : couponIds) {
            processCouponRequestsForCoupon(couponId);
        }
    }

    private void processCouponRequestsForCoupon(Long couponId) {
        Long maxIssued = couponService.getMaxIssued(couponId);
        Long issuedCount = couponService.getIssuedCouponCount(couponId);
        List<Long> userIds = new ArrayList<>();

        while (issuedCount < maxIssued) {
            Long userId = couponService.dequeueCouponRequest(couponId);
            if (userId == null) break;

            userIds.add(userId);

            boolean issued = couponService.tryIssueCoupon(userId, couponId, maxIssued);
            if (!issued) break;

            issuedCount++;
        }

        couponService.insertUserCouponList(userIds, couponId);
    }
}
