package br.com.farmacia.integration.ans;

public interface AnsPort {
    AnsResult validatePrescription(String prescriptionId);

    record AnsResult(String status) {}
}
