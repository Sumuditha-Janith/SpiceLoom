package lk.ijse.gdse71.spiceloom.repository;

import lk.ijse.gdse71.spiceloom.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderOrderId(int orderId);
}