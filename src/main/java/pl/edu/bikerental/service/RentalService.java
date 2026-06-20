package pl.edu.bikerental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.bikerental.domain.criteria.RentalCriteria;
import pl.edu.bikerental.repository.RentalRepository;
import pl.edu.bikerental.service.dto.RentalDTO;
import pl.edu.bikerental.service.mapper.RentalMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link pl.edu.bikerental.domain.Rental}.
 */
@Service
@Transactional
public class RentalService {

    private static final Logger LOG = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    private final RentalMapper rentalMapper;

    public RentalService(RentalRepository rentalRepository, RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.rentalMapper = rentalMapper;
    }

    /**
     * Save a rental.
     *
     * @param rentalDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> save(RentalDTO rentalDTO) {
        LOG.debug("Request to save Rental : {}", rentalDTO);
        return rentalRepository.save(rentalMapper.toEntity(rentalDTO)).map(rentalMapper::toDto);
    }

    /**
     * Update a rental.
     *
     * @param rentalDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> update(RentalDTO rentalDTO) {
        LOG.debug("Request to update Rental : {}", rentalDTO);
        return rentalRepository.save(rentalMapper.toEntity(rentalDTO)).map(rentalMapper::toDto);
    }

    /**
     * Partially update a rental.
     *
     * @param rentalDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> partialUpdate(RentalDTO rentalDTO) {
        LOG.debug("Request to partially update Rental : {}", rentalDTO);

        return rentalRepository
            .findById(rentalDTO.getId())
            .map(existingRental -> {
                rentalMapper.partialUpdate(existingRental, rentalDTO);

                return existingRental;
            })
            .flatMap(rentalRepository::save)
            .map(rentalMapper::toDto);
    }

    /**
     * Find rentals by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RentalDTO> findByCriteria(RentalCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Rentals by Criteria");
        return rentalRepository.findByCriteria(criteria, pageable).map(rentalMapper::toDto);
    }

    /**
     * Find the count of rentals by criteria.
     * @param criteria filtering criteria
     * @return the count of rentals
     */
    public Mono<Long> countByCriteria(RentalCriteria criteria) {
        LOG.debug("Request to get the count of all Rentals by Criteria");
        return rentalRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of rentals available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return rentalRepository.count();
    }

    /**
     * Get one rental by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RentalDTO> findOne(Long id) {
        LOG.debug("Request to get Rental : {}", id);
        return rentalRepository.findById(id).map(rentalMapper::toDto);
    }

    /**
     * Delete the rental by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Rental : {}", id);
        return rentalRepository.deleteById(id);
    }
}
