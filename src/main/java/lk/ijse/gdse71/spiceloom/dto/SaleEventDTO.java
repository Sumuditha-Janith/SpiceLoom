package lk.ijse.gdse71.spiceloom.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaleEventDTO {
    private int saleEventId;
    private Integer productId;
    private int discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}