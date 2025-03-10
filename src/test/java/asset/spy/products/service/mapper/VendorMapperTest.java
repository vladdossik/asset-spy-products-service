package asset.spy.products.service.mapper;

import asset.spy.products.service.dto.ResponseVendorDto;
import asset.spy.products.service.dto.SaveVendorDto;
import asset.spy.products.service.dto.UpdateVendorDto;
import asset.spy.products.service.entity.VendorEntity;
import asset.spy.products.service.AbstractInitialization;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class VendorMapperTest extends AbstractInitialization {

    private final VendorMapper vendorMapper = new VendorMapperImpl();

    @Test
    void saveVendorDtoToVendorEntityTest() {
        SaveVendorDto vendorDto = saveVendorDto;

        VendorEntity vendorEntity = vendorMapper.toVendorEntity(vendorDto);

        assertThat(vendorDto)
                .usingRecursiveComparison()
                .isEqualTo(vendorEntity);
    }

    @Test
    void vendorEntityToResponseVendorDtoTest() {
        ResponseVendorDto vendorDto = vendorMapper.toResponseVendorDto(vendor);

        assertThat(vendorDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "products")
                .isEqualTo(vendor);
    }

    @Test
    void updateVendorDtoToVendorEntityTest() {
        UpdateVendorDto vendorDto = updateVendorDto;
        VendorEntity vendorEntity = vendor;

        vendorMapper.updateVendor(vendorDto, vendorEntity);

        assertThat(vendorDto)
                .usingRecursiveComparison()
                .isEqualTo(vendorEntity);
    }
}