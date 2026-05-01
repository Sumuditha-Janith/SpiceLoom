package lk.ijse.gdse71.spiceloom.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartDTO {
    private int cartId;
    private List<CartItemDTO> items;
    private double total;
}