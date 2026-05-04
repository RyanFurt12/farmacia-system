package br.com.farmacia.integration.ans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AnsMockAdapter implements AnsPort {

    private static final Logger log = LoggerFactory.getLogger(AnsMockAdapter.class);
    private final Random random = new Random();

    @Override
    public AnsResult validatePrescription(String prescriptionId) {
        log.info("[ANS-MOCK] Validating prescription: {}", prescriptionId);

        if (random.nextInt(10) == 0) {
            log.warn("[ANS-MOCK] Prescription REJECTED: {}", prescriptionId);
            return new AnsResult("REJECTED");
        }

        log.info("[ANS-MOCK] Prescription APPROVED: {}", prescriptionId);
        return new AnsResult("APPROVED");
    }
}
