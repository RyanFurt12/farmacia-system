package br.com.farmacia.controller;

import br.com.farmacia.dto.OrderResponse;
import br.com.farmacia.model.Product;
import br.com.farmacia.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> register(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.register(product));
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("/{id}/order")
    public ResponseEntity<OrderResponse> orderProduct(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.orderProduct(id, quantity));
    }
}
