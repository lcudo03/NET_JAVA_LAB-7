package pl.edu.bikerental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.bikerental.domain.criteria.CustomerProfileCriteria;
import pl.edu.bikerental.repository.CustomerProfileRepository;
import pl.edu.bikerental.service.dto.CustomerProfileDTO;
import pl.edu.bikerental.service.mapper.CustomerProfileMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link pl.edu.bikerental.domain.CustomerProfile}.
 */
@Service
@Transactional
public class CustomerProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerProfileService.class);

    private final CustomerProfileRepository customerProfileRepository;

    private final CustomerProfileMapper customerProfileMapper;

    public CustomerProfileService(CustomerProfileRepository customerProfileRepository, CustomerProfileMapper customerProfileMapper) {
        this.customerProfileRepository = customerProfileRepository;
        this.customerProfileMapper = customerProfileMapper;
    }

    /**
     * Save a customerProfile.
     *
     * @param customerProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerProfileDTO> save(CustomerProfileDTO customerProfileDTO) {
        LOG.debug("Request to save CustomerProfile : {}", customerProfileDTO);
        return customerProfileRepository.save(customerProfileMapper.toEntity(customerProfileDTO)).map(customerProfileMapper::toDto);
    }

    /**
     * Update a customerProfile.
     *
     * @param customerProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerProfileDTO> update(CustomerProfileDTO customerProfileDTO) {
        LOG.debug("Request to update CustomerProfile : {}", customerProfileDTO);
        return customerProfileRepository.save(customerProfileMapper.toEntity(customerProfileDTO)).map(customerProfileMapper::toDto);
    }

    /**
     * Partially update a customerProfile.
     *
     * @param customerProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CustomerProfileDTO> partialUpdate(CustomerProfileDTO customerProfileDTO) {
        LOG.debug("Request to partially update CustomerProfile : {}", customerProfileDTO);

        return customerProfileRepository
            .findById(customerProfileDTO.getId())
            .map(existingCustomerProfile -> {
                customerProfileMapper.partialUpdate(existingCustomerProfile, customerProfileDTO);

                return existingCustomerProfile;
            })
            .flatMap(customerProfileRepository::save)
            .map(customerProfileMapper::toDto);
    }

    /**
     * Find customerProfiles by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerProfileDTO> findByCriteria(CustomerProfileCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all CustomerProfiles by Criteria");
        return customerProfileRepository.findByCriteria(criteria, pageable).map(customerProfileMapper::toDto);
    }

    /**
     * Find the count of customerProfiles by criteria.
     * @param criteria filtering criteria
     * @return the count of customerProfiles
     */
    public Mono<Long> countByCriteria(CustomerProfileCriteria criteria) {
        LOG.debug("Request to get the count of all CustomerProfiles by Criteria");
        return customerProfileRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of customerProfiles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerProfileRepository.count();
    }

    /**
     * Get one customerProfile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CustomerProfileDTO> findOne(Long id) {
        LOG.debug("Request to get CustomerProfile : {}", id);
        return customerProfileRepository.findById(id).map(customerProfileMapper::toDto);
    }

    /**
     * Delete the customerProfile by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete CustomerProfile : {}", id);
        return customerProfileRepository.deleteById(id);
    }
}
