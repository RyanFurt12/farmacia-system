package br.com.farmacia.repository;

import br.com.farmacia.model.SaleItem;
import br.com.farmacia.repository.projection.TopProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    @Query("SELECT si.productId AS productId, si.productName AS productName, " +
           "SUM(si.quantity) AS totalQuantity " +
           "FROM SaleItem si " +
           "GROUP BY si.productId, si.productName " +
           "ORDER BY SUM(si.quantity) DESC")
    List<TopProductProjection> findTopProducts();
}
