package lk.ijse.gdse71.spiceloom.service;

import lk.ijse.gdse71.spiceloom.dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO dto, MultipartFile imageFile);
    ProductDTO updateProduct(int id, ProductDTO dto, MultipartFile imageFile);
    void deleteProduct(int id);
    ProductDTO getProduct(int id);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategory(String category);
}