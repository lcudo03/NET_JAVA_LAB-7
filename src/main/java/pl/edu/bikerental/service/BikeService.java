package pl.edu.bikerental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.bikerental.domain.criteria.BikeCriteria;
import pl.edu.bikerental.repository.BikeRepository;
import pl.edu.bikerental.service.dto.BikeDTO;
import pl.edu.bikerental.service.mapper.BikeMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link pl.edu.bikerental.domain.Bike}.
 */
@Service
@Transactional
public class BikeService {

    private static final Logger LOG = LoggerFactory.getLogger(BikeService.class);

    private final BikeRepository bikeRepository;

    private final BikeMapper bikeMapper;

    public BikeService(BikeRepository bikeRepository, BikeMapper bikeMapper) {
        this.bikeRepository = bikeRepository;
        this.bikeMapper = bikeMapper;
    }

    /**
     * Save a bike.
     *
     * @param bikeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BikeDTO> save(BikeDTO bikeDTO) {
        LOG.debug("Request to save Bike : {}", bikeDTO);
        return bikeRepository.save(bikeMapper.toEntity(bikeDTO)).map(bikeMapper::toDto);
    }

    /**
     * Update a bike.
     *
     * @param bikeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BikeDTO> update(BikeDTO bikeDTO) {
        LOG.debug("Request to update Bike : {}", bikeDTO);
        return bikeRepository.save(bikeMapper.toEntity(bikeDTO)).map(bikeMapper::toDto);
    }

    /**
     * Partially update a bike.
     *
     * @param bikeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BikeDTO> partialUpdate(BikeDTO bikeDTO) {
        LOG.debug("Request to partially update Bike : {}", bikeDTO);

        return bikeRepository
            .findById(bikeDTO.getId())
            .map(existingBike -> {
                bikeMapper.partialUpdate(existingBike, bikeDTO);

                return existingBike;
            })
            .flatMap(bikeRepository::save)
            .map(bikeMapper::toDto);
    }

    /**
     * Find bikes by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BikeDTO> findByCriteria(BikeCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Bikes by Criteria");
        return bikeRepository.findByCriteria(criteria, pageable).map(bikeMapper::toDto);
    }

    /**
     * Find the count of bikes by criteria.
     * @param criteria filtering criteria
     * @return the count of bikes
     */
    public Mono<Long> countByCriteria(BikeCriteria criteria) {
        LOG.debug("Request to get the count of all Bikes by Criteria");
        return bikeRepository.countByCriteria(criteria);
    }

    /**
     * Get all the bikes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<BikeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bikeRepository.findAllWithEagerRelationships(pageable).map(bikeMapper::toDto);
    }

    /**
     * Returns the number of bikes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return bikeRepository.count();
    }

    /**
     * Get one bike by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BikeDTO> findOne(Long id) {
        LOG.debug("Request to get Bike : {}", id);
        return bikeRepository.findOneWithEagerRelationships(id).map(bikeMapper::toDto);
    }

    /**
     * Delete the bike by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Bike : {}", id);
        return bikeRepository.deleteById(id);
    }
}
