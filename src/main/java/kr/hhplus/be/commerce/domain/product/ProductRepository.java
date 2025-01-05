package kr.hhplus.be.commerce.domain.product;

public interface ProductRepository {

    Product findById(Long productId);

    Long decreaseStockWithLock(Long productId, Long quantity);
}
