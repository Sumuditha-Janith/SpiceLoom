package lk.ijse.gdse71.spiceloom.controller;

import lk.ijse.gdse71.spiceloom.dto.SaleEventDTO;
import lk.ijse.gdse71.spiceloom.service.SaleEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/sales")
public class AdminSaleEventController {

    private final SaleEventService saleEventService;

    public AdminSaleEventController(SaleEventService saleEventService) {
        this.saleEventService = saleEventService;
    }

    @PostMapping
    public ResponseEntity<?> createSaleEvent(@RequestBody SaleEventDTO dto) {
        try {
            SaleEventDTO created = saleEventService.createSaleEvent(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSaleEvent(@PathVariable int id, @RequestBody SaleEventDTO dto) {
        try {
            SaleEventDTO updated = saleEventService.updateSaleEvent(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSaleEvent(@PathVariable int id) {
        saleEventService.deleteSaleEvent(id);
        return ResponseEntity.ok(Map.of("message", "Sale event deleted"));
    }

    @GetMapping
    public ResponseEntity<List<SaleEventDTO>> getAll() {
        return ResponseEntity.ok(saleEventService.getAllSaleEvents());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SaleEventDTO>> getActive() {
        return ResponseEntity.ok(saleEventService.getActiveSaleEvents());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SaleEventDTO>> getByProduct(@PathVariable int productId) {
        return ResponseEntity.ok(saleEventService.getSaleEventsByProduct(productId));
    }

}