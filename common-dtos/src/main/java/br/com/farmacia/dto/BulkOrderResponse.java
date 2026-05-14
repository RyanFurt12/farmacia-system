package br.com.farmacia.dto;

public class BulkOrderResponse {

    private String protocol;
    private String status;
    private String message;
    private int totalItems;

    public BulkOrderResponse() {}

    public BulkOrderResponse(String protocol, String status, String message, int totalItems) {
        this.protocol = protocol;
        this.status = status;
        this.message = message;
        this.totalItems = totalItems;
    }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
}
