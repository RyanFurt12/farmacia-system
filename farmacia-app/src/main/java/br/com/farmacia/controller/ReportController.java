package br.com.farmacia.controller;

import br.com.farmacia.repository.projection.TopProductProjection;
import br.com.farmacia.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public ResponseEntity<ReportService.SalesReport> salesByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(reportService.salesByPeriod(start, end));
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductProjection>> topProducts() {
        return ResponseEntity.ok(reportService.topProducts());
    }
}
