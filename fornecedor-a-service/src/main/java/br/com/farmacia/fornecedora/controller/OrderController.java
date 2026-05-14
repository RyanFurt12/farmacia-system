package br.com.farmacia.fornecedora.controller;

import br.com.farmacia.dto.BulkOrderRequest;
import br.com.farmacia.dto.BulkOrderResponse;
import br.com.farmacia.dto.OrderRequest;
import br.com.farmacia.dto.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final Random random = new Random();

    @PostMapping
    public ResponseEntity<OrderResponse> receiveOrder(@RequestBody OrderRequest request) {
        String orderId = UUID.randomUUID().toString().substring(0, 8);

        if (random.nextInt(100) < 15) {
            writeToFile(orderId, request, "ERRO");
            return ResponseEntity.ok(
                    new OrderResponse(null, "ERRO", "COBOL-ERR: Estoque insuficiente")
            );
        }

        String protocol = "FA-" + UUID.randomUUID().toString();
        writeToFile(orderId, request, "SUCESSO");

        return ResponseEntity.ok(
                new OrderResponse(protocol, "SUCESSO", "Pedido aceito")
        );
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkOrderResponse> receiveBulkOrder(@RequestBody BulkOrderRequest request) {
        log.info("[FORNECEDOR-A] Recebendo pedido em lote com {} itens", request.getItems().size());

        for (OrderRequest item : request.getItems()) {
            String orderId = UUID.randomUUID().toString().substring(0, 8);
            writeToFile(orderId, item, "LOTE");
        }

        if (random.nextInt(100) < 15) {
            return ResponseEntity.ok(
                    new BulkOrderResponse(null, "ERRO", "COBOL-ERR: Falha no processamento em lote", request.getItems().size())
            );
        }

        String protocol = "FA-BULK-" + UUID.randomUUID().toString();
        return ResponseEntity.ok(
                new BulkOrderResponse(protocol, "SUCESSO", "Pedido em lote aceito", request.getItems().size())
        );
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Fornecedor A (COBOL) — Operacional");
    }

    private void writeToFile(String orderId, OrderRequest order, String status) {
        log.info("PROCESSAMENTO|{}|{}|{}|{}",
            orderId,
            order.getProductName(),
            order.getQuantity(),
            status);
    }
}
