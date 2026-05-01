package lk.ijse.gdse71.spiceloom.controller;

import lk.ijse.gdse71.spiceloom.dto.ReviewDTO;
import lk.ijse.gdse71.spiceloom.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final lk.ijse.gdse71.spiceloom.repository.UserRepository userRepository;

    public ReviewController(ReviewService reviewService,
                            lk.ijse.gdse71.spiceloom.repository.UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    private int getUserId(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow().getUserId();
    }

    @PostMapping
    public ResponseEntity<?> addReview(Principal principal,
                                       @RequestBody Map<String, Object> body) {
        try {
            int productId = Integer.parseInt(body.get("productId").toString());
            int rating = Integer.parseInt(body.get("rating").toString());
            String comment = (String) body.get("comment");
            ReviewDTO dto = reviewService.addReview(getUserId(principal), productId, rating, comment);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable int productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
}