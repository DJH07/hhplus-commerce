package kr.hhplus.be.commerce.infra.product;

import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {


    @Override
    public Product findById(Long productId) {

        return null;
    }

    @Override
    public Long decreaseStockWithLock(Long productId, Long quantity) {
        return null;
    }
}
