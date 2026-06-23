package br.com.farmacia.repository.projection;

public interface TopProductProjection {
    Long getProductId();
    String getProductName();
    Long getTotalQuantity();
}
