package kr.hhplus.be.commerce.domain.product;


import lombok.RequiredArgsConstructor;

/**
 * 상품 상태
 */
@RequiredArgsConstructor
public enum ProductStatus {
    AVAILABLE("AVAILABLE", "판매 가능"),
    OUT_OF_STOCK("OUT_OF_STOCK", "품절"),
    TEMPORARILY_OUT_OF_STOCK("TEMPORARILY_OUT_OF_STOCK", "임시 품절"),
    DISCONTINUED("DISCONTINUED", "단종");
    private final String type;
    private final String name;

}