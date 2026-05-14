package br.com.farmacia.controller;

import br.com.farmacia.model.PurchaseIntention;
import br.com.farmacia.model.utils.PurchaseIntentionStatus;
import br.com.farmacia.service.PurchaseIntentionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-intentions")
public class PurchaseIntentionController {

    private final PurchaseIntentionService purchaseIntentionService;

    public PurchaseIntentionController(PurchaseIntentionService purchaseIntentionService) {
        this.purchaseIntentionService = purchaseIntentionService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseIntention>> findAll(
            @RequestParam(required = false) PurchaseIntentionStatus status) {
        if (status != null) {
            return ResponseEntity.ok(purchaseIntentionService.findByStatus(status));
        }
        return ResponseEntity.ok(purchaseIntentionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseIntention> findById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseIntentionService.findById(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PurchaseIntention> approve(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseIntentionService.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PurchaseIntention> reject(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseIntentionService.reject(id));
    }

    @PostMapping("/bulk-purchase")
    public ResponseEntity<PurchaseIntentionService.BulkPurchaseSummary> bulkPurchase() {
        return ResponseEntity.ok(purchaseIntentionService.executeBulkPurchase());
    }
}
