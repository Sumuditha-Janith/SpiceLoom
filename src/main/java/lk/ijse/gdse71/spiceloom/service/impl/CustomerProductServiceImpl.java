package lk.ijse.gdse71.spiceloom.service.impl;

import lk.ijse.gdse71.spiceloom.dto.ProductCatalogDTO;
import lk.ijse.gdse71.spiceloom.dto.SaleEventDTO;
import lk.ijse.gdse71.spiceloom.entity.Product;
import lk.ijse.gdse71.spiceloom.repository.ProductRepository;
import lk.ijse.gdse71.spiceloom.service.CustomerProductService;
import lk.ijse.gdse71.spiceloom.service.SaleEventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerProductServiceImpl implements CustomerProductService {

    private final ProductRepository productRepository;
    private final SaleEventService saleEventService;

    public CustomerProductServiceImpl(ProductRepository productRepository,
                                      SaleEventService saleEventService) {
        this.productRepository = productRepository;
        this.saleEventService = saleEventService;
    }

    @Override
    public List<ProductCatalogDTO> getProducts(String sort, boolean onSaleOnly) {
        List<Product> products = productRepository.findAll();
        List<SaleEventDTO> activeSales = saleEventService.getActiveSaleEvents();

        List<ProductCatalogDTO> catalog = products.stream()
                .map(product -> mapToCatalogDTO(product, activeSales))
                .collect(Collectors.toList());

        // Filter onSale only
        if (onSaleOnly) {
            catalog = catalog.stream()
                    .filter(ProductCatalogDTO::isOnSale)
                    .collect(Collectors.toList());
        }

        // Sorting
        if (sort != null) {
            switch (sort) {
                case "price_asc":
                    catalog.sort(Comparator.comparingDouble(ProductCatalogDTO::getDiscountedPrice));
                    break;
                case "price_desc":
                    catalog.sort(Comparator.comparingDouble(ProductCatalogDTO::getDiscountedPrice).reversed());
                    break;
                case "name_asc":
                    catalog.sort(Comparator.comparing(ProductCatalogDTO::getName));
                    break;
                case "name_desc":
                    catalog.sort(Comparator.comparing(ProductCatalogDTO::getName).reversed());
                    break;
                default:
                    break;
            }
        }

        return catalog;
    }

    @Override
    public ProductCatalogDTO getProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        List<SaleEventDTO> activeSales = saleEventService.getActiveSaleEvents();
        return mapToCatalogDTO(product, activeSales);
    }

    private ProductCatalogDTO mapToCatalogDTO(Product product, List<SaleEventDTO> activeSales) {
        double originalPrice = product.getPrice();
        double discountedPrice = originalPrice;
        int discountPercent = 0;
        boolean onSale = false;

        LocalDateTime now = LocalDateTime.now();

        // Find best discount for this product (product-specific or global)
        for (SaleEventDTO sale : activeSales) {
            if (sale.getStartDate().isBefore(now) && sale.getEndDate().isAfter(now)) {
                // Match product-specific or global (productId == null)
                if (sale.getProductId() == null || sale.getProductId() == product.getProductId()) {
                    if (sale.getDiscountPercent() > discountPercent) {
                        discountPercent = sale.getDiscountPercent();
                        onSale = true;
                    }
                }
            }
        }

        if (onSale) {
            discountedPrice = originalPrice * (1 - discountPercent / 100.0);
        }

        return new ProductCatalogDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                originalPrice,
                discountedPrice,
                discountPercent,
                product.getQuantity(),
                product.getCategory(),
                product.getImageUrl(),
                onSale
        );
    }
}