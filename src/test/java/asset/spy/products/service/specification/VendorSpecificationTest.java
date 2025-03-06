package asset.spy.products.service.specification;

import asset.spy.products.service.entity.VendorEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VendorSpecificationTest {

    @Mock
    private Root<VendorEntity> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<?> query;

    @Test
    void hasName_nonNullName_returnSpecificationWithLikePredicate() {
        String name = "name";
        Predicate expected = mock(Predicate.class);

        when(cb.like(root.get("name"), "%" + name.toLowerCase() + "%")).thenReturn(expected);

        Specification<VendorEntity> specification = VendorSpecification.hasName(name);
        Predicate result = specification.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(expected);
        verify(cb, times(1)).like(root.get("name"), "%" + name.toLowerCase() + "%");
    }

    @Test
    void hasName_nullName_returnsNull() {
        Specification<VendorEntity> specification = VendorSpecification.hasName(null);
        Predicate result = specification.toPredicate(root, query, cb);

        assertThat(result).isNull();
    }

    @Test
    void hasCountry_nonNullCountry_returnSpecificationWithLikePredicate() {
        String country = "country";
        Predicate expected = mock(Predicate.class);

        when(cb.like(root.get("country"), "%" + country.toLowerCase() + "%")).thenReturn(expected);

        Specification<VendorEntity> specification = VendorSpecification.hasCountry(country);
        Predicate result = specification.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(expected);
        verify(cb, times(1)).like(root.get("country"), "%" + country.toLowerCase() + "%");
    }

    @Test
    void hasCountry_nullCountry_returnsNull() {
        Specification<VendorEntity> specification = VendorSpecification.hasCountry(null);
        Predicate result = specification.toPredicate(root, query, cb);
        assertThat(result).isNull();
    }
}
