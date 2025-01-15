package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductStatus;
import kr.hhplus.be.commerce.domain.product.ProductStock;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import kr.hhplus.be.commerce.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

class OrderConcurrencyIntegrationTest extends IntegrationTest {
    @Autowired
    private OrderFacade orderFacade;

    @Test
    @DisplayName("주문 동시성 테스트")
    void order_ConcurrentRequests_ShouldHandleCorrectly() throws InterruptedException {
        // given
        Product product = productJpaRepository.save(TestUtils.createTestProduct(1L, ProductStatus.AVAILABLE, 100L));
        ProductStock productStock = ProductStock.create(product.getProductId(), product.getStock());
        productStockJpaRepository.save(productStock);

        User user = userJpaRepository.save(TestUtils.createTestUser());

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<OrderProductItemRequest> items = List.of(new OrderProductItemRequest(product.getProductId(), 10L));

        // when
        List<Future<Long>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    return orderFacade.order(user.getUserId(), items);
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();
        executorService.shutdown();

        // then
        List<Long> orderIds = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                orderIds.add(future.get());
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof BusinessException businessException) {
                    Assertions.assertEquals(BusinessErrorCode.PRODCUT_STOCK_NOT_FOUND, businessException.getErrorCode());
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }

        long successfulOrders = orderIds.stream().filter(Objects::nonNull).count();
        Assertions.assertEquals(10, successfulOrders);

        Product updatedProduct = productJpaRepository.findById(product.getProductId()).orElseThrow();
        Assertions.assertEquals(0L, updatedProduct.getStock());
    }

}
