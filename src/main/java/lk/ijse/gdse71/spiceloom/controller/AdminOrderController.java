package lk.ijse.gdse71.spiceloom.controller;

import lk.ijse.gdse71.spiceloom.dto.OrderDTO;
import lk.ijse.gdse71.spiceloom.enums.OrderStatus;
import lk.ijse.gdse71.spiceloom.service.OrderService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable int orderId, @RequestBody Map<String, String> body) {
        try {
            OrderStatus status = OrderStatus.valueOf(body.get("status"));
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(Map.of("message", "Status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/report")
    public ResponseEntity<InputStreamResource> downloadSalesReport() {
        ByteArrayInputStream bis = orderService.generateSalesReportPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=sales_report.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}