package lk.ijse.gdse71.spiceloom.controller;

import lk.ijse.gdse71.spiceloom.dto.ProductCatalogDTO;
import lk.ijse.gdse71.spiceloom.service.CustomerProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/products")
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    public CustomerProductController(CustomerProductService customerProductService) {
        this.customerProductService = customerProductService;
    }

    // Get all products with optional sorting and filter
    @GetMapping
    public ResponseEntity<List<ProductCatalogDTO>> getAll(
            @RequestParam(required = false) String sort,    // price_asc, price_desc, name_asc, name_desc
            @RequestParam(required = false, defaultValue = "false") boolean onSale
    ) {
        return ResponseEntity.ok(customerProductService.getProducts(sort, onSale));
    }

    // Get single product details
    @GetMapping("/{id}")
    public ResponseEntity<ProductCatalogDTO> getById(@PathVariable int id) {
        return ResponseEntity.ok(customerProductService.getProductById(id));
    }
}