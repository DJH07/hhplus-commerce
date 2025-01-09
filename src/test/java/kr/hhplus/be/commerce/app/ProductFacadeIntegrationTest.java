package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.ProductResponse;
import kr.hhplus.be.commerce.app.dto.TopProductResponse;
import kr.hhplus.be.commerce.domain.order.Order;
import kr.hhplus.be.commerce.domain.order.OrderItem;
import kr.hhplus.be.commerce.domain.order.OrderStatus;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductStatus;
import kr.hhplus.be.commerce.domain.product.ProductStock;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class ProductFacadeIntegrationTest extends IntegrationTest {
    @Autowired
    private ProductFacade productFacade;

    @Test
    @DisplayName("상품 조회 성공 시, 올바른 페이지 반환")
    void getProductPage_ShouldReturnProductPage_WhenSuccessful() {
        // given
        int page = 0;
        int size = 5;
        int productCnt = 2;
        List<Product> savedProducts = new ArrayList<>();

        for(int i = 0; i < productCnt; i++) {
            long index = i + 1;
            Product product = Product.create("Product" + index, 1000L * index, ProductStatus.AVAILABLE, "Description" + index, index);
            Product savedProduct = productJpaRepository.save(product);
            productStockJpaRepository.save(ProductStock.create(savedProduct.getProductId(), index));
            savedProducts.add(savedProduct);
        }

        // when
        Page<ProductResponse> productPage = productFacade.getProductPage(size, page);

        // then
        Assertions.assertNotNull(productPage);
        Assertions.assertEquals(productCnt, productPage.getTotalElements());
        Assertions.assertEquals((int) Math.ceil((double) productCnt / size), productPage.getTotalPages());
        Assertions.assertEquals(productCnt, productPage.getContent().size());
        for (Product savedProduct : savedProducts) {
            Assertions.assertTrue(productPage.getContent().stream()
                    .anyMatch(productResponse -> productResponse.productId().equals(savedProduct.getProductId())));
        }
    }

    @Test
    @DisplayName("상품 조회 시 페이지 번호가 잘못되면 빈 페이지 반환")
    void getProductPage_ShouldReturnEmptyPage_WhenPageNumberIsOutOfRange() {
        // given
        int page = 100;
        int size = 5;

        // when
        Page<ProductResponse> productPage = productFacade.getProductPage(size, page);

        // then
        Assertions.assertNotNull(productPage);
        Assertions.assertTrue(productPage.getContent().isEmpty());
    }

    @Test
    @DisplayName("상품 6개 중 상위 5개 상품이 올바르게 반환")
    void getTopProductList_ShouldReturnTop5Products_WhenThereAre6Products() {
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

        // when
        List<TopProductResponse> topProductList = productFacade.getTopProductList();

        // then
        Assertions.assertNotNull(topProductList);
        Assertions.assertEquals(topProductSize, topProductList.size());

        for (int rank = 0; rank < topProductSize; rank++) {
            TopProductResponse topProduct = topProductList.get(rank);
            Product expectedProduct = savedProducts.get(totalProducts - 1 - rank);

            Assertions.assertEquals(expectedProduct.getProductId(), topProduct.productId());
            Assertions.assertEquals("Product" + (totalProducts - rank), topProduct.name());
            Assertions.assertEquals(1000L * (totalProducts - rank), topProduct.price());
            Assertions.assertEquals("Description" + (totalProducts - rank), topProduct.description());
            Assertions.assertEquals((totalProducts - rank) * 10L, topProduct.soldQuantity());
            Assertions.assertEquals(rank + 1, topProduct.rank());
        }
    }

}
