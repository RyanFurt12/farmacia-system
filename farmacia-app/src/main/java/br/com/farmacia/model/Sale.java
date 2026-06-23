package br.com.farmacia.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = true)
    @JsonBackReference("client-sales")
    private Client client;

    @Column(name = "invoice_cpf")
    private String invoiceCpf;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    private BigDecimal total;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "final_total")
    private BigDecimal finalTotal;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference("sale-items")
    @Builder.Default
    private List<SaleItem> items = new ArrayList<>();

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_status")
    private String invoiceStatus;

    @Column(name = "prescription_id")
    private String prescriptionId;

    @Column(name = "prescription_status")
    private String prescriptionStatus;

    @Transient
    private Long clientId;

    @Transient
    private String clientCpf;

    @PostLoad
    public void postLoad() {
        if (client != null) {
            this.clientId = client.getId();
            this.clientCpf = client.getCpf();
        }
    }

    @PrePersist
    public void prePersist() {
        if (saleDate == null) {
            saleDate = LocalDateTime.now();
        }
    }
}
