package br.com.farmacia.service;

import br.com.farmacia.model.Sale;
import br.com.farmacia.repository.SaleItemRepository;
import br.com.farmacia.repository.SaleRepository;
import br.com.farmacia.repository.projection.TopProductProjection;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    public ReportService(SaleRepository saleRepository, SaleItemRepository saleItemRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
    }

    public SalesReport salesByPeriod(LocalDateTime start, LocalDateTime end) {
        List<Sale> sales = saleRepository.findBySaleDateBetween(start, end);

        BigDecimal gross = sales.stream()
                .map(s -> s.getTotal() == null ? BigDecimal.ZERO : s.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal net = sales.stream()
                .map(s -> s.getFinalTotal() == null ? BigDecimal.ZERO : s.getFinalTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SalesReport(sales.size(), gross, net, sales);
    }

    public List<TopProductProjection> topProducts() {
        return saleItemRepository.findTopProducts();
    }

    public record SalesReport(int totalSales, BigDecimal grossTotal, BigDecimal netTotal, List<Sale> sales) {}
}
