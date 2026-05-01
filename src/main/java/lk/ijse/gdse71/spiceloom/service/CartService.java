package lk.ijse.gdse71.spiceloom.service;

import lk.ijse.gdse71.spiceloom.dto.CartDTO;

public interface CartService {
    CartDTO getCart(int userId);
    CartDTO addItem(int userId, int productId, int quantity);
    CartDTO updateItemQuantity(int userId, int cartItemId, int newQuantity);
    CartDTO removeItem(int userId, int cartItemId);
    void checkout(int userId);
}