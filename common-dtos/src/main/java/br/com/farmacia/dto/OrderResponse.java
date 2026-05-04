package br.com.farmacia.dto;

public class OrderResponse {

    private String protocol;
    private String status;
    private String message;

    public OrderResponse() {}

    public OrderResponse(String protocol, String status, String message) {
        this.protocol = protocol;
        this.status = status;
        this.message = message;
    }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
