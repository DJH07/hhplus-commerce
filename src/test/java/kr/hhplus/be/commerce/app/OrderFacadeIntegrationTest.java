package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import kr.hhplus.be.commerce.domain.order.Order;
import kr.hhplus.be.commerce.domain.order.OrderItemResult;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductStatus;
import kr.hhplus.be.commerce.domain.product.ProductStock;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestProduct;
import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;

class OrderFacadeIntegrationTest extends IntegrationTest {
    @Autowired
    private OrderFacade orderFacade;

    @Test
    @DisplayName("주문 성공 시 주문 정보와 상품 재고가 정상 처리")
    void order_ShouldProcessOrder_WhenSuccessful() {
        // given
        User user = userJpaRepository.save(createTestUser());

        int productCount = 2;
        List<Product> products = new ArrayList<>();
        List<OrderProductItemRequest> orderItems = new ArrayList<>();

        for (int i = 0; i < productCount; i++) {
            Product product = createTestProduct(i + 1L, ProductStatus.AVAILABLE, (i + 1) * 10L);
            Product savedProduct = productJpaRepository.save(product);
            ProductStock productStock = ProductStock.create(savedProduct.getProductId(), product.getStock());
            productStockJpaRepository.save(productStock);
            products.add(product);

            orderItems.add(new OrderProductItemRequest(product.getProductId(), (long) i + 1));
        }

        // when
        Long orderId = orderFacade.order(user.getUserId(), orderItems);

        // then
        Order savedOrder = orderRepository.findById(orderId);
        Assertions.assertNotNull(savedOrder);
        Assertions.assertEquals(user.getUserId(), savedOrder.getUserId());

        long expectedTotalAmount = 0;
        List<OrderItemResult> savedOrderItems = orderItemRepository.findAllByOrderId(orderId);
        Assertions.assertEquals(productCount, savedOrderItems.size());

        for (int i = 0; i < productCount; i++) {
            Product product = products.get(i);
            OrderItemResult savedItem = savedOrderItems.stream()
                    .filter(item -> item.productId() == product.getProductId())
                    .findFirst().orElseThrow();

            long itemTotalPrice = product.getPrice() * orderItems.get(i).quantity();
            expectedTotalAmount += itemTotalPrice;

            Assertions.assertEquals(orderItems.get(i).quantity(), savedItem.quantity());
            Assertions.assertEquals(itemTotalPrice, savedItem.totalPrice());
        }

        Assertions.assertEquals(expectedTotalAmount, savedOrder.getOrderTotalAmount());

        for (int i = 0; i < productCount; i++) {
            Product updatedProduct = productJpaRepository.findById(products.get(i).getProductId()).orElseThrow();
            long expectedStock = products.get(i).getStock() - orderItems.get(i).quantity();
            Assertions.assertEquals(expectedStock, updatedProduct.getStock());
        }
    }


}
