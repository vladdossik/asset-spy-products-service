package asset.spy.products.service.mapper;

import asset.spy.products.service.dto.ResponseProductDto;
import asset.spy.products.service.entity.ProductEntity;
import asset.spy.products.service.service.AbstractInitialization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductMapperTest extends AbstractInitialization {

    private final ProductMapper productMapper = new ProductMapperImpl();

    @Test
    void toProductEntity_validSaveProduct_ReturnProductEntity() {
        ProductEntity productEntity = productMapper.toProduct(saveProductDto);

        assertThat(productEntity).isNotNull();
        assertThat(productEntity.getName()).isEqualTo(saveProductDto.getName());
        assertThat(productEntity.getType()).isEqualTo(saveProductDto.getType());
        assertThat(productEntity.getDescription()).isEqualTo(saveProductDto.getDescription());
        assertThat(productEntity.getManufacturer()).isEqualTo(saveProductDto.getManufacturer());
        assertThat(productEntity.getPrice()).isEqualTo(saveProductDto.getPrice());
        assertThat(productEntity.getExternalId()).isNotNull();
    }

    @Test
    void toResponseProductDto_validProductEntity_ReturnResponseProductDto() {
        ResponseProductDto responseProductDtoResult = productMapper.toResponseProductDto(product);

        assertThat(responseProductDtoResult).isNotNull();
        assertThat(responseProductDtoResult.getId()).isEqualTo(product.getExternalId());
        assertThat(responseProductDtoResult.getName()).isEqualTo(product.getName());
        assertThat(responseProductDtoResult.getType()).isEqualTo(product.getType());
        assertThat(responseProductDtoResult.getDescription()).isEqualTo(product.getDescription());
        assertThat(responseProductDtoResult.getManufacturer()).isEqualTo(product.getManufacturer());
        assertThat(responseProductDtoResult.getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    void updateProduct_validUpdateProduct_ReturnProductEntity() {
        updateProductDto.setId(productId);

        productMapper.updateProduct(updateProductDto, product);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(updateProductDto.getName());
        assertThat(product.getType()).isEqualTo(updateProductDto.getType());
        assertThat(product.getDescription()).isEqualTo(updateProductDto.getDescription());
        assertThat(product.getManufacturer()).isEqualTo(updateProductDto.getManufacturer());
        assertThat(product.getPrice()).isEqualTo(updateProductDto.getPrice());
    }
}
