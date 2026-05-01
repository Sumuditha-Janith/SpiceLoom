package lk.ijse.gdse71.spiceloom.service.impl;

import lk.ijse.gdse71.spiceloom.dto.ReviewDTO;
import lk.ijse.gdse71.spiceloom.entity.*;
import lk.ijse.gdse71.spiceloom.repository.*;
import lk.ijse.gdse71.spiceloom.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ReviewDTO addReview(int userId, int productId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new RuntimeException("Rating must be between 1 and 5");
        if (reviewRepository.existsByUserUserIdAndProductProductId(userId, productId)) {
            throw new RuntimeException("You already reviewed this product");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review = reviewRepository.save(review);
        return new ReviewDTO(review.getReviewId(), productId, userId, user.getUsername(), rating, comment);
    }

    @Override
    public List<ReviewDTO> getReviewsByProduct(int productId) {
        return reviewRepository.findByProductProductId(productId).stream()
                .map(r -> new ReviewDTO(r.getReviewId(), r.getProduct().getProductId(), r.getUser().getUserId(), r.getUser().getUsername(), r.getRating(), r.getComment()))
                .collect(Collectors.toList());
    }
}