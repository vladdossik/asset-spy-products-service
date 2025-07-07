package asset.spy.products.service.service;

import asset.spy.products.service.dto.http.vendor.ResponseVendorDto;
import asset.spy.products.service.dto.http.vendor.CreateVendorDto;
import asset.spy.products.service.dto.http.vendor.UpdateVendorDto;
import asset.spy.products.service.entity.VendorEntity;
import asset.spy.products.service.event.CreatedNewVendorEvent;
import asset.spy.products.service.event.DeletedVendorEvent;
import asset.spy.products.service.event.UpdatedVendorEvent;
import asset.spy.products.service.exception.EntityAlreadyExistsException;
import asset.spy.products.service.mapper.VendorMapper;
import asset.spy.products.service.repository.VendorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "vendor")
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    private final SpecificationCreateService specificationCreateService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public Page<ResponseVendorDto> getVendors(int page, int size, String sortCriteria,
                                              String name, String country) {

        log.info("Get vendors from page {} of size {}", page, size);

        Specification<VendorEntity> specification = specificationCreateService
                .getVendorSpecification(name, country);
        return vendorRepository
                .findAll(specification, PageRequest.of(page, size, Sort.by(sortCriteria)))
                .map(vendorMapper::toResponseVendorDto);
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public ResponseVendorDto getVendorById(UUID id) {
        log.info("Start getting vendor with id {}", id);

        VendorEntity vendor = findVendorById(id);

        return vendorMapper.toResponseVendorDto(vendor);
    }

    @CachePut(key = "#vendor.name")
    @Transactional
    public ResponseVendorDto saveVendor(CreateVendorDto vendor) {
        log.info("Received vendor to save : {}", vendor);

        try {
            VendorEntity vendorToSave = vendorMapper.toVendorEntity(vendor);
            log.info("Vendor to save : {}", vendorToSave);

            VendorEntity savedVendor = vendorRepository.save(vendorToSave);

            applicationEventPublisher.publishEvent(new CreatedNewVendorEvent());
            log.info("Published new CreatedNewVendorEvent");

            return vendorMapper.toResponseVendorDto(savedVendor);
        } catch (DataIntegrityViolationException e) {
            log.error("Error saving vendor", e);
            throw new EntityAlreadyExistsException("This vendor already exists", e.getCause());
        }
    }

    @CachePut(key = "#vendorDto.id")
    @Transactional
    public ResponseVendorDto updateVendor(UpdateVendorDto vendorDto) {
        log.info("Received vendor to update : {}", vendorDto);

        VendorEntity vendor = findVendorById(vendorDto.getId());

        vendorMapper.updateVendor(vendorDto, vendor);

        log.info("Updated vendor : {}", vendor);

        applicationEventPublisher.publishEvent(new UpdatedVendorEvent());
        log.info("Published new UpdatedVendorEvent");

        return vendorMapper.toResponseVendorDto(vendor);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public ResponseVendorDto deleteVendor(UUID id) {
        log.info("Received id to delete : {}", id);

        VendorEntity vendor = findVendorById(id);

        vendorRepository.delete(vendor);
        log.info("Vendor with id {} was deleted", id);

        applicationEventPublisher.publishEvent(new DeletedVendorEvent());
        log.info("Published new DeletedVendorEvent");

        return vendorMapper.toResponseVendorDto(vendor);
    }

    public VendorEntity findVendorById(UUID id) {
        log.info("Getting vendor with id {}", id);
        return vendorRepository
                .findByExternalId(id)
                .orElseThrow(() -> new EntityNotFoundException("This vendor does not exist"));
    }
}