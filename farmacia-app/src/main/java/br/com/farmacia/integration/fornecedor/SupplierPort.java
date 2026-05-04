package br.com.farmacia.integration.fornecedor;

import br.com.farmacia.dto.OrderRequest;
import br.com.farmacia.dto.OrderResponse;
import br.com.farmacia.model.SupplierType;

public interface SupplierPort {
    boolean supports(SupplierType type);
    OrderResponse sendOrder(OrderRequest order);
}
