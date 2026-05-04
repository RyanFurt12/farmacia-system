package br.com.farmacia.dto;

import java.util.List;

public class SaleRequest {

    private String cpf;
    private List<SaleItemRequest> items;
    private String prescriptionId;

    public SaleRequest() {}

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public List<SaleItemRequest> getItems() { return items; }
    public void setItems(List<SaleItemRequest> items) { this.items = items; }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
}
