package br.com.farmacia.model;

import br.com.farmacia.model.utils.PurchaseIntentionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_intentions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseIntention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseIntentionStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;

    @Column(name = "supplier_protocol")
    private String supplierProtocol;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PurchaseIntentionStatus.PENDING;
        }
    }
}
