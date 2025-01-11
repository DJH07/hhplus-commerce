package kr.hhplus.be.commerce.domain.product;

import kr.hhplus.be.commerce.domain.order.OrderItemResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public OrderProductResult processOrderProducts(List<OrderProductItemCommand> productItemCommands) {
        reduceProductsStock(productItemCommands);

        return getOrderProductResult(productItemCommands);
    }

    public void reduceProductsStock(List<OrderProductItemCommand> productItemCommands) {
        for(OrderProductItemCommand command : productItemCommands) {
            Product product = productRepository.findById(command.productId());

            Long remainingStock = decreaseStock(product.getProductId(), command.quantity());
            product.changeStock(remainingStock);
            if(remainingStock < 1) {
                product.changeStatus(ProductStatus.TEMPORARILY_OUT_OF_STOCK);
            }
        }
    }

    public OrderProductResult getOrderProductResult(List<OrderProductItemCommand> productItemCommands) {
        long totalAmount = 0L;
        List<OrderProductItemResult> itemList = new ArrayList<>();
        for(OrderProductItemCommand command : productItemCommands) {
            Product product = productRepository.findById(command.productId());

            long totalPrice = product.getPrice() * command.quantity();
            OrderProductItemResult result = new OrderProductItemResult(command.productId(), command.quantity(), totalPrice);
            itemList.add(result);

            totalAmount += totalPrice;
        }
        return new OrderProductResult(totalAmount, itemList);
    }


    public Page<ProductResult> getProductPage(Integer size, Integer page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "productId"));
        return productRepository.findAllProductResults(pageable);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void restoreProduct(List<OrderItemResult> itemResultList) {
        for(OrderItemResult item : itemResultList) {
            Product product = productRepository.findById(item.productId());
            Long remainingStock = increaseStock(item.productId(), item.quantity());
            product.changeStock(remainingStock);
            if(product.getStatus().equals(ProductStatus.TEMPORARILY_OUT_OF_STOCK)) {
                product.changeStatus(ProductStatus.AVAILABLE);
            }
        }
    }


    private Long decreaseStock(Long productId, Long amount) {
        ProductStock stock = productRepository.findStockByIdWithLock(productId);
        Long remainingStock = stock.getRemainingStock() - amount;
        stock.changeRemainingStock(remainingStock);
        return remainingStock;
    }
    private Long increaseStock(Long productId, Long amount) {
        ProductStock stock = productRepository.findStockByIdWithLock(productId);
        Long remainingStock = stock.getRemainingStock() + amount;
        stock.changeRemainingStock(remainingStock);
        return remainingStock;
    }

    public List<TopProductResult> getTopProductList() {
        return productRepository.getTopProductResults();
    }
}
