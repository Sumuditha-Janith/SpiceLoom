package lk.ijse.gdse71.spiceloom.repository;

import lk.ijse.gdse71.spiceloom.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductProductId(int productId);
    boolean existsByUserUserIdAndProductProductId(int userId, int productId);
}