package asset.spy.products.service.service;

import asset.spy.products.service.entity.ProductEntity;
import asset.spy.products.service.entity.VendorEntity;
import asset.spy.products.service.services.SpecificationCreateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class SpecificationCreateServiceTest extends AbstractInitialization {

    @InjectMocks
    private SpecificationCreateService specificationCreateService;

    @Test
    void getProductSpecification() {
        Specification<ProductEntity> specification = specificationCreateService.getProductSpecification(NAME, TYPE,
                MANUFACTURER, MIN_PRICE, MAX_PRICE);
        assertThat(specification).isNotNull();
    }

    @Test
    void getVendorSpecification() {
        Specification<VendorEntity> specification = specificationCreateService.getVendorSpecification(NAME, COUNTRY);
        assertThat(specification).isNotNull();
    }
}
