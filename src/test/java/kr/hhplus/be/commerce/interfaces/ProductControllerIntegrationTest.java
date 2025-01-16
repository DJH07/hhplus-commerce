package kr.hhplus.be.commerce.interfaces;

import kr.hhplus.be.commerce.domain.order.Order;
import kr.hhplus.be.commerce.domain.order.OrderItem;
import kr.hhplus.be.commerce.domain.order.OrderStatus;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductStatus;
import kr.hhplus.be.commerce.utils.TestUtils;
import kr.hhplus.be.commerce.utils.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("상품 목록 조회 시 유효성 검사 실패")
    void listProduct_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // given
        int invalidSize = -1;
        int invalidPage = -1;

        // when & then
        mockMvc.perform(get("/api/v1/products/list")
                        .param("size", String.valueOf(invalidSize))
                        .param("page", String.valueOf(invalidPage)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("유효성 검사 실패")));
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void listProduct_ShouldReturnProducts_WhenValidRequest() throws Exception {
        // given
        for (long i = 0; i < 15; i++) {
            productJpaRepository.save(TestUtils.createTestProduct(i + 1, ProductStatus.AVAILABLE, 100L));
        }

        int size = 10;
        int page = 0;

        // when & then
        mockMvc.perform(get("/api/v1/products/list")
                        .param("size", String.valueOf(size))
                        .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(size));
    }

    @Test
    @DisplayName("상위 주문 상품 목록 조회 성공")
    void topProduct_ShouldReturnTopProducts_WhenValidRequest() throws Exception {
        // given
        int totalProducts = 6;
        int topProductSize = 5;
        List<Product> savedProducts = new ArrayList<>();

        for (int i = 0; i < totalProducts; i++) {
            long productIndex = i + 1;

            Product product = Product.create(
                    "Product" + productIndex,
                    1000L * productIndex,
                    ProductStatus.AVAILABLE,
                    "Description" + productIndex,
                    100L * productIndex
            );
            Product savedProduct = productJpaRepository.save(product);
            savedProducts.add(savedProduct);

            Order order = Order.create(
                    1L,
                    5000L,
                    LocalDateTime.now().minusDays(1)
            );
            order.changeStatus(OrderStatus.PAID);
            order.changePayDate(LocalDateTime.now().minusDays(1));
            Order savedOrder = orderJpaRepository.save(order);

            OrderItem orderItem = OrderItem.create(
                    savedOrder.getOrderId(),
                    savedProduct.getProductId(),
                    productIndex * 10,
                    savedProduct.getPrice() * productIndex * 10
            );
            orderItemJpaRepository.save(orderItem);
        }

        // when & then
        mockMvc.perform(get("/api/v1/products/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상위 상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(topProductSize));
    }


}
