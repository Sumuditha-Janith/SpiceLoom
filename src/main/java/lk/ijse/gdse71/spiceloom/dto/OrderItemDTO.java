package lk.ijse.gdse71.spiceloom.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItemDTO {
    private int orderItemId;
    private int productId;
    private String productName;
    private int quantity;
    private double priceAtTime;
}