package pl.edu.bikerental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.bikerental.domain.criteria.CustomerCriteria;
import pl.edu.bikerental.repository.CustomerRepository;
import pl.edu.bikerental.service.dto.CustomerDTO;
import pl.edu.bikerental.service.mapper.CustomerMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link pl.edu.bikerental.domain.Customer}.
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> save(CustomerDTO customerDTO) {
        LOG.debug("Request to save Customer : {}", customerDTO);
        return customerRepository.save(customerMapper.toEntity(customerDTO)).map(customerMapper::toDto);
    }

    /**
     * Update a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> update(CustomerDTO customerDTO) {
        LOG.debug("Request to update Customer : {}", customerDTO);
        return customerRepository.save(customerMapper.toEntity(customerDTO)).map(customerMapper::toDto);
    }

    /**
     * Partially update a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> partialUpdate(CustomerDTO customerDTO) {
        LOG.debug("Request to partially update Customer : {}", customerDTO);

        return customerRepository
            .findById(customerDTO.getId())
            .map(existingCustomer -> {
                customerMapper.partialUpdate(existingCustomer, customerDTO);

                return existingCustomer;
            })
            .flatMap(customerRepository::save)
            .map(customerMapper::toDto);
    }

    /**
     * Find customers by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> findByCriteria(CustomerCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Customers by Criteria");
        return customerRepository.findByCriteria(criteria, pageable).map(customerMapper::toDto);
    }

    /**
     * Find the count of customers by criteria.
     * @param criteria filtering criteria
     * @return the count of customers
     */
    public Mono<Long> countByCriteria(CustomerCriteria criteria) {
        LOG.debug("Request to get the count of all Customers by Criteria");
        return customerRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the customers where Profile is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> findAllWhereProfileIsNull() {
        LOG.debug("Request to get all customers where Profile is null");
        return customerRepository.findAllWhereProfileIsNull().map(customerMapper::toDto);
    }

    /**
     * Returns the number of customers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerRepository.count();
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CustomerDTO> findOne(Long id) {
        LOG.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Customer : {}", id);
        return customerRepository.deleteById(id);
    }
}
