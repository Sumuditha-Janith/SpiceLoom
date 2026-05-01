package lk.ijse.gdse71.spiceloom.service.impl;

import lk.ijse.gdse71.spiceloom.dto.SaleEventDTO;
import lk.ijse.gdse71.spiceloom.entity.Product;
import lk.ijse.gdse71.spiceloom.entity.SaleEvent;
import lk.ijse.gdse71.spiceloom.repository.ProductRepository;
import lk.ijse.gdse71.spiceloom.repository.SaleEventRepository;
import lk.ijse.gdse71.spiceloom.service.SaleEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SaleEventServiceImpl implements SaleEventService {

    private final SaleEventRepository saleEventRepository;
    private final ProductRepository productRepository;

    public SaleEventServiceImpl(SaleEventRepository saleEventRepository,
                                ProductRepository productRepository) {
        this.saleEventRepository = saleEventRepository;
        this.productRepository = productRepository;
    }

    @Override
    public SaleEventDTO createSaleEvent(SaleEventDTO dto) {
        validateDates(dto);
        SaleEvent event = mapToEntity(dto);
        event = saleEventRepository.save(event);
        return mapToDto(event);
    }

    @Override
    public SaleEventDTO updateSaleEvent(int id, SaleEventDTO dto) {
        validateDates(dto);
        SaleEvent event = saleEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale event not found"));
        event.setDiscountPercent(dto.getDiscountPercent());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setActive(dto.isActive());
        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            event.setProduct(product);
        } else {
            event.setProduct(null);
        }
        event = saleEventRepository.save(event);
        return mapToDto(event);
    }

    @Override
    public void deleteSaleEvent(int id) {
        saleEventRepository.deleteById(id);
    }

    @Override
    public List<SaleEventDTO> getAllSaleEvents() {
        return saleEventRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleEventDTO> getActiveSaleEvents() {
        return saleEventRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleEventDTO> getSaleEventsByProduct(int productId) {
        return saleEventRepository.findByProductProductId(productId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void validateDates(SaleEventDTO dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new RuntimeException("Start and end dates are required");
        }
        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
    }

    private SaleEvent mapToEntity(SaleEventDTO dto) {
        SaleEvent event = new SaleEvent();
        event.setDiscountPercent(dto.getDiscountPercent());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setActive(dto.isActive());
        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            event.setProduct(product);
        }
        return event;
    }

    private SaleEventDTO mapToDto(SaleEvent event) {
        return new SaleEventDTO(
                event.getSaleEventId(),
                event.getProduct() != null ? event.getProduct().getProductId() : null,
                event.getDiscountPercent(),
                event.getStartDate(),
                event.getEndDate(),
                event.isActive()
        );
    }
}