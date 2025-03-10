package asset.spy.products.service.specification;

import asset.spy.products.service.entity.ProductEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductSpecificationTest {

    @Mock
    private Root<ProductEntity> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<?> query;

    @ParameterizedTest
    @CsvSource({
            "name, 'test'",
            "type, 'test'",
            "manufacturer, 'test'"
    })
    void specificationHasFieldTest(String field, String value) {
        Predicate expectedPredicate = mock(Predicate.class);
        String methodName = "has" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

        when(cb.like(root.get(field), "%" + value + "%")).thenReturn(expectedPredicate);

        Specification<ProductEntity> specification = switch (methodName) {
            case "hasName" -> ProductSpecification.hasName(value);
            case "hasType" -> ProductSpecification.hasType(value);
            case "hasManufacturer" -> ProductSpecification.hasManufacturer(value);
            default -> null;
        };
        Predicate result = specification.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(expectedPredicate);
        verify(cb, times(1)).like(root.get(field), "%" + value + "%");
    }

    @ParameterizedTest
    @CsvSource({
            "name, 'test'",
            "type, 'test'",
            "manufacturer, 'test'"
    })
    void specificationHasNullValueFieldsTest(String field) {
        Specification<ProductEntity> specification = switch (field) {
            case "name" -> ProductSpecification.hasName(null);
            case "type" -> ProductSpecification.hasType(null);
            case "manufacturer" -> ProductSpecification.hasManufacturer(null);
            default -> null;
        };
        Predicate result = specification.toPredicate(root, query, cb);

        assertThat(result).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "maxPrice, 100",
            "minPrice, 50"
    })
    void specificationHasPriceFieldTest(String field, BigDecimal value) {
        Predicate expectedPredicate = mock(Predicate.class);
        switch (field) {
            case "maxPrice":
                when(cb.lessThanOrEqualTo(root.get("price"), value)).thenReturn(expectedPredicate);
                break;
            case "minPrice":
                when(cb.greaterThanOrEqualTo(root.get("price"), value)).thenReturn(expectedPredicate);
                break;
        }

        Specification<ProductEntity> specification = switch (field) {
            case "maxPrice" -> ProductSpecification.maxPrice(value);
            case "minPrice" -> ProductSpecification.minPrice(value);
            default -> null;
        };

        Predicate result = specification.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(expectedPredicate);

        switch (field) {
            case "maxPrice":
                verify(cb, times(1)).lessThanOrEqualTo(root.get("price"), value);
                break;
            case "minPrice":
                verify(cb, times(1)).greaterThanOrEqualTo(root.get("price"), value);
                break;
            default:
                verifyNoInteractions(cb);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "maxPrice, 100",
            "minPrice, 50"
    })
    void specificationHasNullValuePriceFieldTest(String field) {
        Specification<ProductEntity> specification = switch (field) {
            case "maxPrice" -> ProductSpecification.maxPrice(null);
            case "minPrice" -> ProductSpecification.minPrice(null);
            default -> null;
        };
        Predicate result = specification.toPredicate(root, query, cb);
        assertThat(result).isNull();
    }
}
