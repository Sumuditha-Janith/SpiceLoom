package lk.ijse.gdse71.spiceloom.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDTO {
    private int productId;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String category;
    private String imageUrl;
}