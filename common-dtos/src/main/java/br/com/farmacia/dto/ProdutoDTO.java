package br.com.farmacia.dto;

import java.math.BigDecimal;

public class ProdutoDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private Boolean controlled;
    private Integer stock;
    private String supplier;

    public ProdutoDTO() {}

    public ProdutoDTO(Long id, String name, BigDecimal price, Boolean controlled, Integer stock, String supplier) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.controlled = controlled;
        this.stock = stock;
        this.supplier = supplier;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Boolean getControlled() { return controlled; }
    public void setControlled(Boolean controlled) { this.controlled = controlled; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
}
