package kr.hhplus.be.commerce.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Product findById(Long productId);

    Long decreaseStockWithLock(Long productId, Long quantity);

    Page<ProductResult> findAllProductResults(Pageable pageable);
}
