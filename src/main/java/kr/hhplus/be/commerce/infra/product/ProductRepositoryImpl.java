package kr.hhplus.be.commerce.infra.product;

import jakarta.persistence.LockTimeoutException;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import kr.hhplus.be.commerce.domain.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private ProductJpaRepository productJpaRepository;
    private ProductStockJpaRepository productStockJpaRepository;

    @Override
    public Product findById(Long productId) {
        return productJpaRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODCUT_NOT_FOUND));
    }

    @Override
    public ProductStock findStockByIdWithLock(Long productId) {
        try {
            return productStockJpaRepository.findByProductIdWithLock(productId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODCUT_STOCK_NOT_FOUND));
        } catch (LockTimeoutException e) {
            throw new BusinessException(BusinessErrorCode.LOCK_TIMEOUT);
        }
    }

    @Override
    public Page<ProductResult> findAllProductResults(Pageable pageable) {
        return productJpaRepository.findAllProductResults(pageable);
    }

    @Override
    public List<TopProductResult> getTopProductResults() {
        int SIZE = 5;
        return null;
    }


}
