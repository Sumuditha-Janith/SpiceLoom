package lk.ijse.gdse71.spiceloom.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartItemDTO {
    private int cartItemId;
    private int productId;
    private String productName;
    private String imageUrl;
    private double price;
    private double discountedPrice;
    private int discountPercent;
    private int quantity;
    private int availableStock;
}