package asset.spy.products.service.mapper;

import asset.spy.products.service.dto.ResponseVendorDto;
import asset.spy.products.service.dto.UpdateVendorDto;
import asset.spy.products.service.entity.VendorEntity;
import asset.spy.products.service.service.AbstractInitialization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class VendorMapperTest extends AbstractInitialization {

    private final VendorMapper vendorMapper = new VendorMapperImpl();

    @Test
    void toVendorEntity_ValidSaveVendorDto_ReturnsVendorEntity() {
        VendorEntity vendorEntityResult = vendorMapper.toVendorEntity(saveVendorDto);

        assertThat(vendorEntityResult).isNotNull();
        assertThat(vendorEntityResult.getName()).isEqualTo(saveVendorDto.getName());
        assertThat(vendorEntityResult.getCountry()).isEqualTo(saveVendorDto.getCountry());
        assertThat(vendorEntityResult.getExternalId()).isNotNull();
        assertThat(vendorEntityResult.getCreatedAt()).isNotNull();
    }

    @Test
    void toResponseEntity_ValidSaveVendorDto_ReturnsResponseEntity() {
        ResponseVendorDto responseVendorDtoResult = vendorMapper.toResponseVendorDto(vendor);
        assertThat(responseVendorDtoResult).isNotNull();
        assertThat(responseVendorDtoResult.getId()).isEqualTo(vendor.getExternalId());
        assertThat(responseVendorDtoResult.getName()).isEqualTo(vendor.getName());
        assertThat(responseVendorDtoResult.getCountry()).isEqualTo(vendor.getCountry());
    }

    @Test
    void updateVendor_ValidUpdateVendorDto_UpdateVendorDto() {
        updateVendorDto.setId(vendorId);

        vendorMapper.updateVendor(updateVendorDto, vendor);
        assertThat(vendor).isNotNull();
        assertThat(vendor.getName()).isEqualTo(updateVendorDto.getName());
        assertThat(vendor.getCountry()).isEqualTo(updateVendorDto.getCountry());
        assertThat(vendor.getCreatedAt()).isNotNull();
    }
}
