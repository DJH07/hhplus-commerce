package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.ProductResponse;
import kr.hhplus.be.commerce.app.dto.TopProductResponse;
import kr.hhplus.be.commerce.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    // TODO : 상품 조회 기능 둘 다 쿼리 개선 후 캐싱

    @Description("상품 조회")
    public Page<ProductResponse> getProductPage(Integer size, Integer page) {

        return productService.getProductPage(size, page)
                .map(productResult ->
                        new ProductResponse(
                                productResult.productId(),
                                productResult.name(),
                                productResult.price(),
                                productResult.description(),
                                productResult.stock()
                        ));
    }

    @Description("주문 상위 상품 조회")
    public List<TopProductResponse> getTopProductList() {

        AtomicInteger rankCounter = new AtomicInteger(1);

        return productService.getTopProductList()
                .stream()
                .map(result -> new TopProductResponse(
                        result.productId(),
                        rankCounter.getAndIncrement(),
                        result.name(),
                        result.price(),
                        result.description(),
                        result.soldQuantity()
                ))
                .toList();

    }
}
