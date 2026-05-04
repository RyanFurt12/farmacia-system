package br.com.farmacia.integration.sefaz;

public interface SefazPort {
    SefazResult generateInvoice(double totalValue);

    record SefazResult(String invoiceNumber, String status) {}
}
