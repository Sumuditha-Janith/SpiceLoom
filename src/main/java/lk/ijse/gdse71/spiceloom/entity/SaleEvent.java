package lk.ijse.gdse71.spiceloom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sale_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int saleEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int discountPercent;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean active;
}