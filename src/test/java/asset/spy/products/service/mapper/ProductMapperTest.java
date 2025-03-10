package asset.spy.products.service.mapper;

import asset.spy.products.service.dto.ResponseProductDto;
import asset.spy.products.service.dto.SaveProductDto;
import asset.spy.products.service.dto.UpdateProductDto;
import asset.spy.products.service.entity.ProductEntity;
import asset.spy.products.service.AbstractInitialization;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ProductMapperTest extends AbstractInitialization {

    private final ProductMapper productMapper = new ProductMapperImpl();

    @Test
    void saveProductDtoToProductEntityTest() {
        SaveProductDto productDto = saveProductDto;

        ProductEntity productEntity = productMapper.toProduct(productDto);

        assertThat(productDto)
                .usingRecursiveComparison()
                .isEqualTo(productEntity);
    }

    @Test
    void productEntityToResponseProductDtoTest() {
        ResponseProductDto productDto = productMapper.toResponseProductDto(product);

        assertThat(productDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "vendor")
                .isEqualTo(product);
    }

    @Test
    void updateProductDtoToProductEntityTest() {
        UpdateProductDto productDto = updateProductDto;
        ProductEntity productEntity = product;

        productMapper.updateProduct(productDto, productEntity);

        assertThat(productDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(productEntity);
    }
}
