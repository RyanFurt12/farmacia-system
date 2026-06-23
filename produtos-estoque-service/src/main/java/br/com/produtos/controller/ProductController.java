package br.com.produtos.controller;

import br.com.produtos.model.Product;
import br.com.produtos.model.PurchaseIntention;
import br.com.produtos.service.ProductService;
import br.com.produtos.service.PurchaseIntentionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final PurchaseIntentionService purchaseIntentionService;

    public ProductController(ProductService productService, PurchaseIntentionService purchaseIntentionService) {
        this.productService = productService;
        this.purchaseIntentionService = purchaseIntentionService;
    }

    @PostMapping
    public ResponseEntity<Product> register(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.register(product));
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/stock")
    public ResponseEntity<List<StockView>> stockReport() {
        List<StockView> stock = productService.findAll().stream()
                .map(p -> new StockView(p.getId(), p.getName(), p.getStock(), p.getControlled()))
                .toList();
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("/{id}/order")
    public ResponseEntity<PurchaseIntention> orderProduct(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseIntentionService.createIntention(id, quantity));
    }

    @PostMapping("/{id}/baixa")
    public ResponseEntity<Product> baixarEstoque(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.decreaseStock(id, quantity));
    }

    public record StockView(Long id, String name, Integer stock, Boolean controlled) {}
}
