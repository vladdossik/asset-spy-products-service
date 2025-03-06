package asset.spy.products.service.service;

import asset.spy.products.service.dto.ResponseVendorDto;
import asset.spy.products.service.entity.VendorEntity;
import asset.spy.products.service.exception.EntityAlreadyExistsException;
import asset.spy.products.service.mapper.VendorMapper;
import asset.spy.products.service.repositories.VendorRepository;
import asset.spy.products.service.services.SpecificationCreateService;
import asset.spy.products.service.services.VendorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VendorServiceTest extends AbstractInitialization {

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private SpecificationCreateService specificationCreateService;

    @InjectMocks
    private VendorService vendorService;

    @Mock
    private VendorMapper vendorMapper;

    @Test
    void saveVendor_validInput_returnVendorDto() {
        when(vendorMapper.toVendorEntity(any())).thenReturn(vendor);
        when(vendorRepository.save(any())).thenReturn(vendor);
        when(vendorMapper.toResponseVendorDto(vendor)).thenReturn(responseVendorDto);

        ResponseVendorDto result = vendorService.saveVendor(saveVendorDto);

        assertThat(result).isEqualTo(responseVendorDto);
        verify(vendorMapper).toVendorEntity(saveVendorDto);
        verify(vendorRepository).save(vendor);
        verify(vendorMapper).toResponseVendorDto(vendor);
    }

    @Test
    void saveVendor_invalidInput_throwException() {
        when(vendorMapper.toVendorEntity(saveVendorDto)).thenReturn(vendor);
        when(vendorRepository.save(vendor)).thenThrow(new DataIntegrityViolationException("Duplicate vendor"));

        assertThrows(EntityAlreadyExistsException.class, () -> vendorService.saveVendor(saveVendorDto));
        verify(vendorMapper).toVendorEntity(saveVendorDto);
        verify(vendorRepository).save(vendor);
        verify(vendorMapper, never()).toResponseVendorDto(vendor);
    }

    @Test
    void updateVendor_validInput_returnVendorDto() {
        updateVendorDto.setId(vendorId);
        when(vendorRepository.findByExternalId(vendorId)).thenReturn(Optional.of(vendor));
        when(vendorMapper.toResponseVendorDto(vendor)).thenReturn(responseVendorDto);

        ResponseVendorDto result = vendorService.updateVendor(updateVendorDto);
        assertThat(result).isEqualTo(responseVendorDto);
        verify(vendorRepository).findByExternalId(vendorId);
        vendorMapper.updateVendor(updateVendorDto, vendor);
        verify(vendorMapper).toResponseVendorDto(vendor);
    }

    @Test
    void updateVendor_NonExistingVendor_throwException() {
        updateVendorDto.setId(vendorId);
        when(vendorRepository.findByExternalId(vendorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vendorService.updateVendor(updateVendorDto));
        verify(vendorRepository).findByExternalId(vendorId);
        verifyNoMoreInteractions(vendorMapper);
    }

    @Test
    void deleteVendor_validInput_returnVendorDto() {
        when(vendorRepository.findByExternalId(vendorId)).thenReturn(Optional.of(vendor));
        when(vendorMapper.toResponseVendorDto(vendor)).thenReturn(responseVendorDto);
        doNothing().when(vendorRepository).delete(vendor);

        ResponseVendorDto result = vendorService.deleteVendor(vendorId);
        assertThat(result).isEqualTo(responseVendorDto);
        verify(vendorRepository).findByExternalId(vendorId);
        verify(vendorRepository).delete(vendor);
        verify(vendorMapper).toResponseVendorDto(vendor);
    }

    @Test
    void deleteVendor_invalidInput_throwException() {
        when(vendorRepository.findByExternalId(vendorId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vendorService.deleteVendor(vendorId));
        verify(vendorRepository).findByExternalId(vendorId);
        verifyNoMoreInteractions(vendorRepository, vendorMapper);
    }

    @Test
    void getVendorById_validInput_returnVendorDto() {
        when(vendorRepository.findByExternalId(vendorId)).thenReturn(Optional.of(vendor));
        when(vendorMapper.toResponseVendorDto(vendor)).thenReturn(responseVendorDto);

        ResponseVendorDto result = vendorService.getVendorById(vendorId);
        assertThat(result).isEqualTo(responseVendorDto);
        verify(vendorRepository).findByExternalId(vendorId);
        verify(vendorMapper).toResponseVendorDto(vendor);
    }

    @Test
    void getVendorById_invalidInput_throwException() {
        when(vendorRepository.findByExternalId(vendorId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vendorService.getVendorById(vendorId));
        verify(vendorRepository).findByExternalId(vendorId);
        verifyNoMoreInteractions(vendorMapper);
    }

    @Test
    void getVendors_validInput_returnPageResult() {
        Page<VendorEntity> vendorPage = new PageImpl<>(Collections.singletonList(vendor), PageRequest.of(PAGE, SIZE, Sort.by(SORT_CRITERIA)), 1);
        Specification<VendorEntity> vendorSpecification = Specification.where(null);

        when(specificationCreateService.getVendorSpecification(NAME, COUNTRY)).thenReturn(vendorSpecification);
        when(vendorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(vendorPage);
        when(vendorMapper.toResponseVendorDto(vendor)).thenReturn(responseVendorDto);

        Page<ResponseVendorDto> result = vendorService.getVendors(PAGE, SIZE, SORT_CRITERIA, NAME, COUNTRY);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(responseVendorDto);

        verify(specificationCreateService).getVendorSpecification(NAME, COUNTRY);
        verify(vendorRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(vendorMapper).toResponseVendorDto(vendor);
    }
}
