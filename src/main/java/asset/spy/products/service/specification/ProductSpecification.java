package asset.spy.products.service.specification;

import asset.spy.products.service.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<ProductEntity> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<ProductEntity> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.like(root.get("type"), "%" + type + "%");
    }

    public static Specification<ProductEntity> hasManufacturer(String manufacturer) {
        return (root, query, criteriaBuilder) ->
                manufacturer == null ? null : criteriaBuilder.equal(root.get("manufacturer"), manufacturer);
    }

    public static Specification<ProductEntity> maxPrice(BigDecimal price) {
        return (root, query, criteriaBuilder) ->
                price == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<ProductEntity> minPrice(BigDecimal price) {
        return (root, query, criteriaBuilder) ->
                price == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price);
    }
}