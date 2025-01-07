package kr.hhplus.be.commerce.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void reduceProductsStock(List<OrderProductItemCommand> productItemCommands) {
        for(OrderProductItemCommand command : productItemCommands) {
            Product product = productRepository.findById(command.productId());

            Long remainingStock = productRepository.decreaseStockWithLock(product.getProductId(), command.quantity());
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
}
