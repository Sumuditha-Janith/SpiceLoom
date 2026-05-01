package lk.ijse.gdse71.spiceloom.service;

import lk.ijse.gdse71.spiceloom.dto.OrderDTO;
import lk.ijse.gdse71.spiceloom.enums.OrderStatus;
import java.io.ByteArrayInputStream;
import java.util.List;

public interface OrderService {
    List<OrderDTO> getOrdersByUser(int userId);
    OrderDTO getOrder(int orderId);
    List<OrderDTO> getAllOrders();
    void updateOrderStatus(int orderId, OrderStatus newStatus);
    ByteArrayInputStream generateReceiptPdf(int orderId);
    ByteArrayInputStream generateSalesReportPdf();
}