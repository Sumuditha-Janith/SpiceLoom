package lk.ijse.gdse71.spiceloom.repository;

import lk.ijse.gdse71.spiceloom.entity.SaleEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleEventRepository extends JpaRepository<SaleEvent, Integer> {
    List<SaleEvent> findByActiveTrue();
    List<SaleEvent> findByProductProductId(int productId);
    List<SaleEvent> findByEndDateBefore(LocalDateTime date);
}