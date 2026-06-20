package pl.edu.bikerental.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import pl.edu.bikerental.domain.criteria.BikeCriteria;
import pl.edu.bikerental.repository.BikeRepository;
import pl.edu.bikerental.service.BikeService;
import pl.edu.bikerental.service.dto.BikeDTO;
import pl.edu.bikerental.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link pl.edu.bikerental.domain.Bike}.
 */
@RestController
@RequestMapping("/api/bikes")
public class BikeResource {

    private static final Logger LOG = LoggerFactory.getLogger(BikeResource.class);

    private static final String ENTITY_NAME = "bike";

    @Value("${jhipster.clientApp.name:bikerentalapp}")
    private String applicationName;

    private final BikeService bikeService;

    private final BikeRepository bikeRepository;

    public BikeResource(BikeService bikeService, BikeRepository bikeRepository) {
        this.bikeService = bikeService;
        this.bikeRepository = bikeRepository;
    }

    /**
     * {@code POST  /bikes} : Create a new bike.
     *
     * @param bikeDTO the bikeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bikeDTO, or with status {@code 400 (Bad Request)} if the bike has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<BikeDTO>> createBike(@Valid @RequestBody BikeDTO bikeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Bike : {}", bikeDTO);
        if (bikeDTO.getId() != null) {
            throw new BadRequestAlertException("A new bike cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return bikeService.save(bikeDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/bikes/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /bikes/:id} : Updates an existing bike.
     *
     * @param id the id of the bikeDTO to save.
     * @param bikeDTO the bikeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bikeDTO,
     * or with status {@code 400 (Bad Request)} if the bikeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bikeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BikeDTO>> updateBike(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BikeDTO bikeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Bike : {}, {}", id, bikeDTO);
        if (bikeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bikeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bikeRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return bikeService
                .update(bikeDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /bikes/:id} : Partial updates given fields of an existing bike, field will ignore if it is null
     *
     * @param id the id of the bikeDTO to save.
     * @param bikeDTO the bikeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bikeDTO,
     * or with status {@code 400 (Bad Request)} if the bikeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bikeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bikeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BikeDTO>> partialUpdateBike(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BikeDTO bikeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Bike partially : {}, {}", id, bikeDTO);
        if (bikeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bikeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bikeRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<BikeDTO> result = bikeService.partialUpdate(bikeDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /bikes} : get all the Bikes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Bikes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<BikeDTO>>> getAllBikes(
        BikeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Bikes by criteria: {}", criteria);
        return bikeService
            .countByCriteria(criteria)
            .zipWith(bikeService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /bikes/count} : count all the bikes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countBikes(BikeCriteria criteria) {
        LOG.debug("REST request to count Bikes by criteria: {}", criteria);
        return bikeService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /bikes/:id} : get the "id" bike.
     *
     * @param id the id of the bikeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bikeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BikeDTO>> getBike(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Bike : {}", id);
        Mono<BikeDTO> bikeDTO = bikeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bikeDTO);
    }

    /**
     * {@code DELETE  /bikes/:id} : delete the "id" bike.
     *
     * @param id the id of the bikeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBike(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Bike : {}", id);
        return bikeService
            .delete(id)

            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
