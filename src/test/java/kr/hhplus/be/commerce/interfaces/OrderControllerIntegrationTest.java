package kr.hhplus.be.commerce.interfaces;

import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductStatus;
import kr.hhplus.be.commerce.domain.product.ProductStock;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.interfaces.dto.request.OrderRequest;
import kr.hhplus.be.commerce.utils.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collections;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestProduct;
import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("주문 요청에서 userId가 null일 경우 유효성 검사 실패")
    void createOrder_ShouldFailValidation_WhenUserIdIsNull() throws Exception {
        // Given
        OrderRequest invalidRequest = OrderRequest.builder()
                .userId(null)
                .items(Collections.singletonList(new OrderProductItemRequest(1L, 2L)))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.code == 'userId')].message").value("사용자 ID는 필수값입니다."));
    }

    @Test
    @DisplayName("주문 요청에서 items가 비어있을 경우 유효성 검사 실패")
    void createOrder_ShouldFailValidation_WhenItemsIsEmpty() throws Exception {
        // Given
        OrderRequest invalidRequest = OrderRequest.builder()
                .userId(1L)
                .items(null)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.code == 'items')].message").value("주문 상품 목록은 필수값입니다."));
    }

    @Test
    @DisplayName("주문 요청에서 items에 상품이 1개 미만일 경우 유효성 검사 실패")
    void createOrder_ShouldFailValidation_WhenItemsSizeIsLessThanOne() throws Exception {
        // Given
        OrderRequest invalidRequest = OrderRequest.builder()
                .userId(1L)
                .items(Collections.singletonList(new OrderProductItemRequest(1L, 0L)))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.code == 'items[0].quantity')].message").value("유효하지 않은 상품 수량입니다."));
    }

    @Test
    @DisplayName("주문 성공 시 정상적인 응답을 반환")
    void createOrder_Success() throws Exception {
        // Given
        User user = userJpaRepository.save(createTestUser());
        Product product = productJpaRepository.save(createTestProduct(1L, ProductStatus.AVAILABLE, 100L));
        productStockJpaRepository.save(ProductStock.create(product.getProductId(), product.getStock()));

        OrderProductItemRequest itemRequest = new OrderProductItemRequest(product.getProductId(), 2L);
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(user.getUserId())
                .items(Collections.singletonList(itemRequest))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("주문이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data").isNumber());
    }
}
