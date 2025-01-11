package kr.hhplus.be.commerce.utils;

import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.DiscountType;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductStatus;
import kr.hhplus.be.commerce.domain.user.User;

import java.time.LocalDateTime;

public abstract class TestUtils {

    public static User createTestUser() {
        return User.create("잘생긴 라이언 코치님");
    }

    public static Coupon createTestCoupon(Long index, LocalDateTime startDate, LocalDateTime endDate, Long maxIssued) {
        return Coupon.create(
                "COUPON_" + index,
                "갓라이언코치님의 최강특강쿠폰 " + index,
                "멋진 라이언 코치님께 " + index + "회 특강을 받는다.",
                DiscountType.PERCENTAGE,
                10L, 100L, startDate, endDate, maxIssued
        );
    }

    public static Product createTestProduct(long index, ProductStatus status, long stock) {
        return Product.create("멋쟁이 라이언 코치님께 질문 고봉밥 스택 " + index,
                index * 1000,
                status,
                "라이언 코치님께 " + index + "번 절하고 성장하기",
                stock);
    }

}
