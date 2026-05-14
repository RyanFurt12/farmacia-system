package br.com.farmacia.repository;

import br.com.farmacia.model.PurchaseIntention;
import br.com.farmacia.model.utils.PurchaseIntentionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseIntentionRepository extends JpaRepository<PurchaseIntention, Long> {
    List<PurchaseIntention> findByStatus(PurchaseIntentionStatus status);
}
