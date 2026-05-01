package lk.ijse.gdse71.spiceloom.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCatalogDTO {
    private int productId;
    private String name;
    private String description;
    private double price;                // original price
    private double discountedPrice;      // after discount (if any)
    private int discountPercent;
    private int quantity;
    private String category;
    private String imageUrl;
    private boolean onSale;
}