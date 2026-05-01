package lk.ijse.gdse71.spiceloom.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDTO {
    private int reviewId;
    private int productId;
    private int userId;
    private String userName;
    private int rating;
    private String comment;
}