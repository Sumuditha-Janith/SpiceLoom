package lk.ijse.gdse71.spiceloom.controller;

import lk.ijse.gdse71.spiceloom.dto.OrderDTO;
import lk.ijse.gdse71.spiceloom.service.OrderService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/orders")
public class CustomerOrderController {

    private final OrderService orderService;
    private final lk.ijse.gdse71.spiceloom.repository.UserRepository userRepository;

    public CustomerOrderController(OrderService orderService,
                                   lk.ijse.gdse71.spiceloom.repository.UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private int getUserId(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getMyOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrdersByUser(getUserId(principal)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable int orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/{orderId}/receipt")
    public ResponseEntity<InputStreamResource> downloadReceipt(@PathVariable int orderId) {
        ByteArrayInputStream bis = orderService.generateReceiptPdf(orderId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=receipt_" + orderId + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}