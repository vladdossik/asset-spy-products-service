package asset.spy.products.service.services;

import asset.spy.products.service.dto.ResponseVendorDto;
import asset.spy.products.service.dto.SaveVendorDto;
import asset.spy.products.service.dto.UpdateVendorDto;
import asset.spy.products.service.entity.VendorEntity;
import asset.spy.products.service.exception.EntityAlreadyExistsException;
import asset.spy.products.service.mapper.VendorMapper;
import asset.spy.products.service.repositories.VendorRepository;
import asset.spy.products.service.specification.VendorSpecification;
import asset.spy.products.service.util.hashing.IDHasher;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;

    @Transactional(readOnly = true)
    public Page<ResponseVendorDto> getVendors(int page, int size, String sortCriteria,
                                              String name, String country) {

        log.info("Get vendors from page {} of size {}", page, size);

        Specification<VendorEntity> specification = Specification
                .where(VendorSpecification.hasName(name)
                        .and(VendorSpecification.hasCountry(country)));
        return vendorRepository
                .findAll(specification, PageRequest.of(page, size, Sort.by(sortCriteria)))
                .map(vendorMapper::toResponseVendorDto);
    }

    @Transactional(readOnly = true)
    public ResponseVendorDto getVendorById(Long id) {
        log.info("Start getting vendor with id {}", id);

        VendorEntity vendor = findVendorById(id);

        return vendorMapper.toResponseVendorDto(vendor);
    }

    @Transactional
    public ResponseVendorDto saveVendor(SaveVendorDto vendor) {
        log.info("Received vendor to save : {}", vendor);

        try {
            VendorEntity vendorToSave = vendorMapper.toVendorEntity(vendor);
            log.info("Vendor to save : {}", vendorToSave);

            return vendorMapper.toResponseVendorDto(vendorRepository.save(vendorToSave));
        } catch (DataIntegrityViolationException e) {
            log.error("Error saving vendor", e);
            throw new EntityAlreadyExistsException("This vendor already exists", e.getCause());
        }
    }

    @Transactional
    public ResponseVendorDto updateVendor(UpdateVendorDto vendorDto) {
        log.info("Received vendor to update : {}", vendorDto);

        VendorEntity vendor = findVendorById(vendorDto.getId());

        vendorMapper.updateVendor(vendor, vendorDto);

        log.info("Updated vendor : {}", vendor);
        return vendorMapper.toResponseVendorDto(vendor);
    }

    @Transactional
    public ResponseVendorDto deleteVendor(Long id) {
        log.info("Received id to delete : {}", id);

        VendorEntity vendor = findVendorById(id);

        vendorRepository.delete(vendor);
        log.info("Vendor with id {} was deleted", id);

        return vendorMapper.toResponseVendorDto(vendor);
    }

    public VendorEntity findVendorById(Long id) {
        log.info("Getting vendor with id {}", id);
        return vendorRepository
                .findById(IDHasher.hashId(id))
                .orElseThrow(() -> new EntityNotFoundException("This vendor does not exist"));
    }
}