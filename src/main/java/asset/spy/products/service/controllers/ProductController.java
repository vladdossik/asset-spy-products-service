package asset.spy.products.service.controllers;

import asset.spy.products.service.dto.ResponseProductDto;
import asset.spy.products.service.dto.SaveProductDto;
import asset.spy.products.service.dto.UpdateProductDto;
import asset.spy.products.service.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/api/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public Page<ResponseProductDto> getProducts(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false, defaultValue = "id") String sortCriteria,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String type,
                                                @RequestParam(required = false) String manufacturer,
                                                @RequestParam(required = false)BigDecimal minPrice,
                                                @RequestParam(required = false) BigDecimal maxPrice) {
        return productService.getProducts(page, size, sortCriteria, name, type, manufacturer, maxPrice, minPrice);
    }

    @GetMapping("/{id}")
    public ResponseProductDto getProduct(@PathVariable long id) {
        return productService.getProduct(id);
    }

    @PostMapping("/save/{vendorId}")
    public ResponseProductDto saveProduct(@Valid @RequestBody SaveProductDto productDto, @PathVariable long vendorId) {
        return productService.saveProduct(productDto, vendorId);
    }

    @PutMapping("/update")
    public ResponseProductDto updateProduct(@Valid @RequestBody UpdateProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseProductDto deleteProduct(@PathVariable long id) {
        return productService.deleteProduct(id);
    }
}