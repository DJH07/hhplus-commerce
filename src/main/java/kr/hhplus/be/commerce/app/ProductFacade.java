package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.product.ProductService;
import kr.hhplus.be.commerce.app.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    @Description("상품 조회")
    public Page<ProductResponse> getProductPage(Integer size, Integer page) {
        return null;
    }
}
