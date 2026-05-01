package lk.ijse.gdse71.spiceloom.controller;

import lk.ijse.gdse71.spiceloom.dto.CartDTO;
import lk.ijse.gdse71.spiceloom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private int getUserId(Principal principal) {
        // principal is username; we need to fetch userId. We'll use UserRepository.
        // Simpler: use a utility method. We'll add a static helper in userService or use Principal.
        // We'll temporarily use UserRepository autowired here.
        return getUserIdFromUsername(principal.getName());
    }

    @Autowired
    private lk.ijse.gdse71.spiceloom.repository.UserRepository userRepository;

    private int getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(getUserId(principal)));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addItem(Principal principal,
                                           @RequestParam int productId,
                                           @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.addItem(getUserId(principal), productId, quantity));
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartDTO> updateQuantity(Principal principal,
                                                  @PathVariable int cartItemId,
                                                  @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(getUserId(principal), cartItemId, quantity));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<CartDTO> removeItem(Principal principal,
                                              @PathVariable int cartItemId) {
        return ResponseEntity.ok(cartService.removeItem(getUserId(principal), cartItemId));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(Principal principal) {
        try {
            cartService.checkout(getUserId(principal));
            return ResponseEntity.ok(Map.of("message", "Order placed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}