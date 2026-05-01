package lk.ijse.gdse71.spiceloom.service.impl;

import lk.ijse.gdse71.spiceloom.dto.*;
import lk.ijse.gdse71.spiceloom.entity.*;
import lk.ijse.gdse71.spiceloom.enums.OrderStatus;
import lk.ijse.gdse71.spiceloom.repository.*;
import lk.ijse.gdse71.spiceloom.service.OrderService;
import lk.ijse.gdse71.spiceloom.util.PdfUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PdfUtil pdfUtil;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            PdfUtil pdfUtil) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.pdfUtil = pdfUtil;
    }

    @Override
    public List<OrderDTO> getOrdersByUser(int userId) {
        return orderRepository.findByUserUserIdOrderByOrderDateDesc(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toDto(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public ByteArrayInputStream generateReceiptPdf(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);
        return pdfUtil.createOrderReceipt(order, items);
    }

    @Override
    public ByteArrayInputStream generateSalesReportPdf() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return pdfUtil.createSalesReport(orders);
    }

    private OrderDTO toDto(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());
        List<OrderItemDTO> itemDTOs = items.stream().map(oi -> new OrderItemDTO(
                oi.getOrderItemId(),
                oi.getProduct().getProductId(),
                oi.getProduct().getName(),
                oi.getQuantity(),
                oi.getPriceAtTime()
        )).collect(Collectors.toList());

        return new OrderDTO(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getUser().getUsername(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                itemDTOs
        );
    }
}