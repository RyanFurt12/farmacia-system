package br.com.produtos.service;

import br.com.produtos.model.Product;
import br.com.produtos.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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


    @Transactional
    public Product decreaseStock(Long id, int quantity) {
        Product product = findById(id);
        int current = product.getStock() == null ? 0 : product.getStock();
        if (current < quantity) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente para o produto " + id + " (disponível: " + current + ", pedido: " + quantity + ")");
        }
        product.setStock(current - quantity);
        log.info("[ESTOQUE] Baixa de {} unidade(s) do produto {} (restam {})", quantity, id, product.getStock());
        return productRepository.save(product);
    }
}
