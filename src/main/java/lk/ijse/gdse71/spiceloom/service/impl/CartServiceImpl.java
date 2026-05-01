package lk.ijse.gdse71.spiceloom.service.impl;

import lk.ijse.gdse71.spiceloom.dto.*;
import lk.ijse.gdse71.spiceloom.entity.*;
import lk.ijse.gdse71.spiceloom.enums.OrderStatus;
import lk.ijse.gdse71.spiceloom.repository.*;
import lk.ijse.gdse71.spiceloom.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SaleEventService saleEventService;

    public CartServiceImpl(UserRepository userRepository,
                           CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           SaleEventService saleEventService) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.saleEventService = saleEventService;
    }

    private Cart getOrCreateCart(int userId) {
        return cartRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow();
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public CartDTO getCart(int userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItemDTO> items = cartItemRepository.findByCartCartId(cart.getCartId())
                .stream().map(this::toCartItemDTO).collect(Collectors.toList());
        double total = items.stream()
                .mapToDouble(i -> i.getDiscountedPrice() * i.getQuantity())
                .sum();
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setItems(items);
        dto.setTotal(total);
        return dto;
    }

    @Override
    public CartDTO addItem(int userId, int productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }
        // Check if item already in cart
        List<CartItem> existingItems = cartItemRepository.findByCartCartId(cart.getCartId());
        for (CartItem item : existingItems) {
            if (item.getProduct().getProductId() == productId) {
                int newQty = item.getQuantity() + quantity;
                if (product.getQuantity() < newQty) {
                    throw new RuntimeException("Not enough stock");
                }
                item.setQuantity(newQty);
                cartItemRepository.save(item);
                return getCart(userId);
            }
        }
        // Add new item
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        cartItemRepository.save(newItem);
        return getCart(userId);
    }

    @Override
    public CartDTO updateItemQuantity(int userId, int cartItemId, int newQuantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        // Validate cart ownership
        if (item.getCart().getUser().getUserId() != userId) {
            throw new RuntimeException("Unauthorized");
        }
        if (newQuantity <= 0) {
            return removeItem(userId, cartItemId);
        }
        if (item.getProduct().getQuantity() < newQuantity) {
            throw new RuntimeException("Not enough stock");
        }
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Override
    public CartDTO removeItem(int userId, int cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (item.getCart().getUser().getUserId() != userId) {
            throw new RuntimeException("Unauthorized");
        }
        cartItemRepository.delete(item);
        return getCart(userId);
    }

    @Override
    public void checkout(int userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemRepository.findByCartCartId(cart.getCartId());
        if (items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;
        User user = userRepository.findById(userId).orElseThrow();
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // create order items and deduct stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : items) {
            Product product = ci.getProduct();
            if (product.getQuantity() < ci.getQuantity()) {
                throw new RuntimeException("Not enough stock for " + product.getName());
            }
            product.setQuantity(product.getQuantity() - ci.getQuantity());
            productRepository.save(product);

            // Get best available discount at time of purchase
            double unitPrice = getEffectivePrice(product);
            total += unitPrice * ci.getQuantity();

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(ci.getQuantity());
            oi.setPriceAtTime(unitPrice);
            orderItems.add(oi);
        }
        order.setTotalAmount(total);
        order = orderRepository.save(order);
        for (OrderItem oi : orderItems) {
            oi.setOrder(order);
            orderItemRepository.save(oi);
        }

        // Clear the cart
        cartItemRepository.deleteByCartCartId(cart.getCartId());
    }

    private CartItemDTO toCartItemDTO(CartItem ci) {
        Product p = ci.getProduct();
        double effectivePrice = getEffectivePrice(p);
        int discount = getDiscountPercent(p);
        double originalPrice = p.getPrice();
        return new CartItemDTO(
                ci.getCartItemId(),
                p.getProductId(),
                p.getName(),
                p.getImageUrl(),
                originalPrice,
                effectivePrice,
                discount,
                ci.getQuantity(),
                p.getQuantity()
        );
    }

    private double getEffectivePrice(Product product) {
        int discount = getDiscountPercent(product);
        if (discount > 0) {
            return product.getPrice() * (1 - discount / 100.0);
        }
        return product.getPrice();
    }

    private int getDiscountPercent(Product product) {
        List<SaleEventDTO> activeSales = saleEventService.getActiveSaleEvents();
        int maxDiscount = 0;
        for (SaleEventDTO sale : activeSales) {
            if (sale.getProductId() == null || sale.getProductId() == product.getProductId()) {
                if (sale.getDiscountPercent() > maxDiscount) {
                    maxDiscount = sale.getDiscountPercent();
                }
            }
        }
        return maxDiscount;
    }
}