package lk.ijse.gdse71.spiceloom.repository;

import lk.ijse.gdse71.spiceloom.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserUserId(int userId);
}