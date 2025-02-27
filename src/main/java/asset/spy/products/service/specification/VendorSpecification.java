package asset.spy.products.service.specification;

import asset.spy.products.service.entity.VendorEntity;
import org.springframework.data.jpa.domain.Specification;

public class VendorSpecification {

    public static Specification<VendorEntity> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<VendorEntity> hasCountry(String country) {
        return ((root, query, criteriaBuilder)
                -> country == null ? null : criteriaBuilder.like(root.get("country"), "%" + country + "%"));
    }
}