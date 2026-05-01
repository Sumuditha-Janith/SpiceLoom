package lk.ijse.gdse71.spiceloom.service;

import lk.ijse.gdse71.spiceloom.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    ReviewDTO addReview(int userId, int productId, int rating, String comment);
    List<ReviewDTO> getReviewsByProduct(int productId);
}