package br.com.farmacia.dto;

import java.util.List;

public class BulkOrderRequest {

    private List<OrderRequest> items;

    public BulkOrderRequest() {}

    public BulkOrderRequest(List<OrderRequest> items) {
        this.items = items;
    }

    public List<OrderRequest> getItems() { return items; }
    public void setItems(List<OrderRequest> items) { this.items = items; }
}
