package kr.hhplus.be.commerce.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository {

    Product findById(Long productId);

    ProductStock findStockByIdWithLock(Long productId);

    Page<ProductResult> findAllProductResults(Pageable pageable);

    List<TopProductResult> getTopProductResults();

}
