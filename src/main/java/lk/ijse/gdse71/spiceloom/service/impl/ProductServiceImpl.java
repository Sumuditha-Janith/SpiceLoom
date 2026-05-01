package lk.ijse.gdse71.spiceloom.service.impl;

import lk.ijse.gdse71.spiceloom.dto.ProductDTO;
import lk.ijse.gdse71.spiceloom.entity.Product;
import lk.ijse.gdse71.spiceloom.repository.ProductRepository;
import lk.ijse.gdse71.spiceloom.service.ProductService;
import lk.ijse.gdse71.spiceloom.util.ImgbbUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImgbbUtil imgbbUtil;

    public ProductServiceImpl(ProductRepository productRepository, ImgbbUtil imgbbUtil) {
        this.productRepository = productRepository;
        this.imgbbUtil = imgbbUtil;
    }

    @Override
    public ProductDTO createProduct(ProductDTO dto, MultipartFile imageFile) {
        Product product = mapToEntity(dto);
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imgbbUtil.uploadImage(imageFile);
            product.setImageUrl(imageUrl);
        }
        product = productRepository.save(product);
        return mapToDto(product);
    }

    @Override
    public ProductDTO updateProduct(int id, ProductDTO dto, MultipartFile imageFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imgbbUtil.uploadImage(imageFile);
            product.setImageUrl(imageUrl);
        }
        product = productRepository.save(product);
        return mapToDto(product);
    }

    @Override
    public void deleteProduct(int id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProduct(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToDto(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Product mapToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
        return product;
    }

    private ProductDTO mapToDto(Product product) {
        return new ProductDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getImageUrl()
        );
    }
}