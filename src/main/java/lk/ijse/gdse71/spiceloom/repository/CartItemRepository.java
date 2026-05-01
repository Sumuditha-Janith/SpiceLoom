package lk.ijse.gdse71.spiceloom.repository;

import lk.ijse.gdse71.spiceloom.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCartCartId(int cartId);
    void deleteByCartCartId(int cartId);
}