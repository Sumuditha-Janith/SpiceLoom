package lk.ijse.gdse71.spiceloom.service;

import lk.ijse.gdse71.spiceloom.dto.SaleEventDTO;
import java.util.List;

public interface SaleEventService {
    SaleEventDTO createSaleEvent(SaleEventDTO dto);
    SaleEventDTO updateSaleEvent(int id, SaleEventDTO dto);
    void deleteSaleEvent(int id);
    List<SaleEventDTO> getAllSaleEvents();
    List<SaleEventDTO> getActiveSaleEvents();
    List<SaleEventDTO> getSaleEventsByProduct(int productId);
}