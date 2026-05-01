package lk.ijse.gdse71.spiceloom.repository;

import lk.ijse.gdse71.spiceloom.entity.Order;
import lk.ijse.gdse71.spiceloom.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserUserIdOrderByOrderDateDesc(int userId);
    List<Order> findAllByOrderByOrderDateDesc();
}