package br.com.produtos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "barcode")
    private String barcode;

    private BigDecimal price;

    @Column(nullable = false)
    private Boolean controlled;

    private Integer stock;

    @Enumerated(EnumType.STRING)
    private SupplierType supplier;
}
