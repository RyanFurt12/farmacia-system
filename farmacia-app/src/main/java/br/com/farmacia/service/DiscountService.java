package br.com.farmacia.service;

import br.com.farmacia.model.Client;
import br.com.farmacia.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;


@Service
public class DiscountService {

    private static final int IDADE_IDOSO = 60;
    private static final int TETO_DESCONTO = 30;

    private final SaleRepository saleRepository;

    public DiscountService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public DiscountResult calculate(Client client, BigDecimal total) {
        if (client == null || client.getId() == null) {
            return new DiscountResult(0, BigDecimal.ZERO);
        }

        int percent = progressivePercent(saleRepository.countByClient(client));

        if (isElderly(client) && Boolean.TRUE.equals(client.getHasInsurance())) {
            percent += 10;
        }

        percent = Math.min(percent, TETO_DESCONTO);

        BigDecimal amount = total
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return new DiscountResult(percent, amount);
    }

    private int progressivePercent(long previousPurchases) {
        if (previousPurchases >= 6) return 10;
        if (previousPurchases >= 3) return 5;
        return 0;
    }

    private boolean isElderly(Client client) {
        LocalDate birth = client.getBirthDate();
        return birth != null && Period.between(birth, LocalDate.now()).getYears() >= IDADE_IDOSO;
    }

    public record DiscountResult(int percent, BigDecimal amount) {}
}
