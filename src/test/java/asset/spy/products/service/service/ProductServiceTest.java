package asset.spy.products.service.service;

import static org.assertj.core.api.Assertions.assertThat;

import asset.spy.products.service.AbstractInitialization;
import asset.spy.products.service.dto.ResponseProductDto;
import asset.spy.products.service.dto.SaveProductDto;
import asset.spy.products.service.entity.ProductEntity;
import asset.spy.products.service.mapper.ProductMapper;
import asset.spy.products.service.repositories.ProductRepository;
import asset.spy.products.service.services.ProductService;
import asset.spy.products.service.services.SpecificationCreateService;
import asset.spy.products.service.services.VendorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest extends AbstractInitialization {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private ProductService productService;

    @Mock
    private SpecificationCreateService specificationCreateService;

    @Test
    void saveProductIfNotExistsTest() {
        when(vendorService.findVendorById(vendorId)).thenReturn(vendor);
        when(productMapper.toProduct(any(SaveProductDto.class))).thenReturn(product);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);
        when(productMapper.toResponseProductDto(product)).thenReturn(responseProductDto);

        ResponseProductDto result = productService.saveProduct(saveProductDto, vendorId);

        assertThat(result).isEqualTo(responseProductDto);
        verify(vendorService).findVendorById(vendorId);
        verify(productMapper).toProduct(saveProductDto);
        verify(productRepository).save(product);
        verify(productMapper).toResponseProductDto(product);
    }

    @Test
    void saveProductIfVendorNotExistTest() {
        when(vendorService.findVendorById(vendorId)).thenThrow(new EntityNotFoundException("Vendor not found"));

        assertThrows(EntityNotFoundException.class, () -> productService.saveProduct(saveProductDto, vendorId));
        verify(vendorService).findVendorById(vendorId);
    }

    @Test
    void updateProductIfProductExistsTest() {
        when(productRepository.findByExternalId(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponseProductDto(product)).thenReturn(responseProductDto);

        ResponseProductDto result = productService.updateProduct(updateProductDto);

        assertThat(result).isEqualTo(responseProductDto);
        verify(productRepository).findByExternalId(productId);
        verify(productMapper).toResponseProductDto(product);
    }

    @Test
    void updateProductIfProductNotExistsTest() {
        when(productRepository.findByExternalId(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(updateProductDto));
        verify(productRepository).findByExternalId(productId);
    }

    @Test
    void deleteProductIfProductExistsTest() {
        when(productRepository.findByExternalId(productId)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(productId);

        verify(productRepository).findByExternalId(productId);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProductIfProductNotExistsTest() {
        when(productRepository.findByExternalId(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteProduct(productId));
        verify(productRepository).findByExternalId(productId);
        verifyNoMoreInteractions(productRepository, productMapper);
    }

    @Test
    void getProductsByVendorIfVendorExistsTest() {
        List<ProductEntity> products = Collections.singletonList(product);
        vendor.setProducts(products);
        when(vendorService.findVendorById(vendorId)).thenReturn(vendor);
        when(productMapper.toResponseProductDto(product)).thenReturn(responseProductDto);

        List<ResponseProductDto> result = productService.getProductsByVendorId(vendorId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(responseProductDto);
        verify(vendorService).findVendorById(vendorId);
        verify(productMapper).toResponseProductDto(product);
    }

    @Test
    void getProductsByVendorIfVendorNotExistsTest() {
        when(vendorService.findVendorById(vendorId)).thenThrow(new EntityNotFoundException("Vendor not found"));

        assertThrows(EntityNotFoundException.class, () -> productService.getProductsByVendorId(vendorId));

        verify(vendorService).findVendorById(vendorId);
        verifyNoMoreInteractions(productMapper);
    }

    @Test
    void getProductsByVendorIfVendorHasNoProductsTest() {
        when(vendorService.findVendorById(vendorId)).thenReturn(vendor);
        vendor.setProducts(Collections.emptyList());
        List<ResponseProductDto> result = productService.getProductsByVendorId(vendorId);

        assertThat(result).isEmpty();

        verify(vendorService).findVendorById(vendorId);
        verifyNoMoreInteractions(productMapper);
    }

    @Test
    void getProductByExternalIdIfProductExistsTest() {
        when(productRepository.findByExternalId(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponseProductDto(product)).thenReturn(responseProductDto);

        ResponseProductDto result = productService.getProduct(productId);

        assertThat(result).isEqualTo(responseProductDto);
        verify(productRepository).findByExternalId(productId);
        verify(productMapper).toResponseProductDto(product);
    }

    @Test
    void getProductByExternalIdIfProductNotExistsTest() {
        when(productRepository.findByExternalId(any(UUID.class))).thenReturn(Optional.empty());
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, ()
                -> productService.getProduct(UUID.randomUUID()));

        assertEquals("Product not found", e.getMessage());
        verify(productRepository).findByExternalId(any(UUID.class));
        verify(productMapper, never()).toResponseProductDto(any());
    }

    @Test
    void getProductsWithoutFiltersTest() {
        Page<ProductEntity> productPage = new PageImpl<>(Collections.singletonList(product),
                PageRequest.of(PAGE, SIZE, Sort.by(SORT_CRITERIA)), 1);
        Specification<ProductEntity> productSpecification = Specification.where(null);

        when(specificationCreateService.getProductSpecification(null, null, null, null, null))
                .thenReturn(productSpecification);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toResponseProductDto(product)).thenReturn(responseProductDto);

        Page<ResponseProductDto> result = productService.getProducts(PAGE, SIZE, SORT_CRITERIA,
                null, null, null, null, null);

        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(responseProductDto);

        verify(specificationCreateService).getProductSpecification(null, null, null, null, null);
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(productMapper).toResponseProductDto(product);
    }

    @Test
    void getProductsWithFiltersTest() {
        Page<ProductEntity> productPage = new PageImpl<>(Collections.singletonList(product),
                PageRequest.of(PAGE, SIZE, Sort.by(SORT_CRITERIA)), 1);
        Specification<ProductEntity> productSpecification = Specification.where(null);

        when(specificationCreateService.getProductSpecification(NAME, TYPE, MANUFACTURER, MIN_PRICE, MAX_PRICE))
                .thenReturn(productSpecification);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toResponseProductDto(product)).thenReturn(responseProductDto);

        Page<ResponseProductDto> result = productService.getProducts(PAGE, SIZE, SORT_CRITERIA,
                NAME, TYPE, MANUFACTURER, MAX_PRICE, MIN_PRICE);

        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(responseProductDto);

        verify(specificationCreateService).getProductSpecification(NAME, TYPE, MANUFACTURER, MIN_PRICE, MAX_PRICE);
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(productMapper).toResponseProductDto(product);
    }
}