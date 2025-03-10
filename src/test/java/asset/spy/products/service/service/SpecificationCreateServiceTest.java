package asset.spy.products.service.service;

import asset.spy.products.service.AbstractInitialization;
import asset.spy.products.service.entity.ProductEntity;
import asset.spy.products.service.entity.VendorEntity;
import asset.spy.products.service.services.SpecificationCreateService;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SpecificationCreateServiceTest extends AbstractInitialization {

    private final SpecificationCreateService specificationCreateService = new SpecificationCreateService();

    @Test
    void getProductSpecificationTest() {
        Specification<ProductEntity> specification = specificationCreateService.getProductSpecification(NAME, TYPE,
                MANUFACTURER, MIN_PRICE, MAX_PRICE);
        assertThat(specification).isNotNull();
    }

    @Test
    void getVendorSpecificationTest() {
        Specification<VendorEntity> specification = specificationCreateService.getVendorSpecification(NAME, COUNTRY);
        assertThat(specification).isNotNull();
    }
}
