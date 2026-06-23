package br.com.produtos.repository;

import br.com.produtos.model.PurchaseIntention;
import br.com.produtos.model.PurchaseIntentionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseIntentionRepository extends JpaRepository<PurchaseIntention, Long> {
    List<PurchaseIntention> findByStatus(PurchaseIntentionStatus status);
}
