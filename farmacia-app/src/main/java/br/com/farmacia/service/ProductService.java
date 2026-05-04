package br.com.farmacia.service;

import br.com.farmacia.dto.OrderRequest;
import br.com.farmacia.dto.OrderResponse;
import br.com.farmacia.integration.fornecedor.SupplierPort;
import br.com.farmacia.model.Product;
import br.com.farmacia.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final List<SupplierPort> supplierPorts;

    public ProductService(ProductRepository productRepository, List<SupplierPort> supplierPorts) {
        this.productRepository = productRepository;
        this.supplierPorts = supplierPorts;
    }

    @Transactional
    public Product register(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
    }

    public OrderResponse orderProduct(Long productId, Integer quantity) {
        Product product = findById(productId);

        SupplierPort supplierPort = supplierPorts.stream()
                .filter(port -> port.supports(product.getSupplier()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum adaptador encontrado para o fornecedor: " + product.getSupplier()));

        OrderRequest orderRequest = new OrderRequest(product.getId(), product.getName(), quantity);
        return supplierPort.sendOrder(orderRequest);
    }
}
