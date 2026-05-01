package lk.ijse.gdse71.spiceloom.service;

import lk.ijse.gdse71.spiceloom.dto.ProductCatalogDTO;
import java.util.List;

public interface CustomerProductService {
    List<ProductCatalogDTO> getProducts(String sort, boolean onSaleOnly);
    ProductCatalogDTO getProductById(int id);
}