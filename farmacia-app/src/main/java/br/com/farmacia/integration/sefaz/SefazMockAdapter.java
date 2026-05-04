package br.com.farmacia.integration.sefaz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class SefazMockAdapter implements SefazPort {

    private static final Logger log = LoggerFactory.getLogger(SefazMockAdapter.class);
    private final Random random = new Random();

    @Override
    public SefazResult generateInvoice(double totalValue) {
        log.info("[SEFAZ-MOCK] Generating invoice for total={}", totalValue);

        String invoiceNumber = UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        if (random.nextInt(10) == 0) {
            log.warn("[SEFAZ-MOCK] Invoice REJECTED: {}", invoiceNumber);
            return new SefazResult(invoiceNumber, "REJECTED");
        }

        log.info("[SEFAZ-MOCK] Invoice AUTHORIZED: {}", invoiceNumber);
        return new SefazResult(invoiceNumber, "AUTHORIZED");
    }
}
