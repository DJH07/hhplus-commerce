package kr.hhplus.be.commerce.infra.product;

import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductRepository;
import kr.hhplus.be.commerce.domain.product.ProductResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private ProductJpaRepository productJpaRepository;

    @Override
    public Product findById(Long productId) {

        return null;
    }

    @Override
    public Long decreaseStockWithLock(Long productId, Long quantity) {
        return null;
    }

    @Override
    public Page<ProductResult> findAllProductResults(Pageable pageable) {
        return productJpaRepository.findAllProductResults(pageable);
    }
}
