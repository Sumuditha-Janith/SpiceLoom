package lk.ijse.gdse71.spiceloom.dto;

import lk.ijse.gdse71.spiceloom.enums.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDTO {
    private int orderId;
    private int userId;
    private String userName;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus status;
    private List<OrderItemDTO> items;
}