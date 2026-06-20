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
import pl.edu.bikerental.domain.criteria.CustomerProfileCriteria;
import pl.edu.bikerental.repository.CustomerProfileRepository;
import pl.edu.bikerental.service.CustomerProfileService;
import pl.edu.bikerental.service.dto.CustomerProfileDTO;
import pl.edu.bikerental.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link pl.edu.bikerental.domain.CustomerProfile}.
 */
@RestController
@RequestMapping("/api/customer-profiles")
public class CustomerProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerProfileResource.class);

    private static final String ENTITY_NAME = "customerProfile";

    @Value("${jhipster.clientApp.name:bikerentalapp}")
    private String applicationName;

    private final CustomerProfileService customerProfileService;

    private final CustomerProfileRepository customerProfileRepository;

    public CustomerProfileResource(CustomerProfileService customerProfileService, CustomerProfileRepository customerProfileRepository) {
        this.customerProfileService = customerProfileService;
        this.customerProfileRepository = customerProfileRepository;
    }

    /**
     * {@code POST  /customer-profiles} : Create a new customerProfile.
     *
     * @param customerProfileDTO the customerProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerProfileDTO, or with status {@code 400 (Bad Request)} if the customerProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<CustomerProfileDTO>> createCustomerProfile(@Valid @RequestBody CustomerProfileDTO customerProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CustomerProfile : {}", customerProfileDTO);
        if (customerProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return customerProfileService.save(customerProfileDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/customer-profiles/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /customer-profiles/:id} : Updates an existing customerProfile.
     *
     * @param id the id of the customerProfileDTO to save.
     * @param customerProfileDTO the customerProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerProfileDTO,
     * or with status {@code 400 (Bad Request)} if the customerProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerProfileDTO>> updateCustomerProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomerProfileDTO customerProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomerProfile : {}, {}", id, customerProfileDTO);
        if (customerProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerProfileRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return customerProfileService
                .update(customerProfileDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /customer-profiles/:id} : Partial updates given fields of an existing customerProfile, field will ignore if it is null
     *
     * @param id the id of the customerProfileDTO to save.
     * @param customerProfileDTO the customerProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerProfileDTO,
     * or with status {@code 400 (Bad Request)} if the customerProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CustomerProfileDTO>> partialUpdateCustomerProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CustomerProfileDTO customerProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomerProfile partially : {}, {}", id, customerProfileDTO);
        if (customerProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerProfileRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<CustomerProfileDTO> result = customerProfileService.partialUpdate(customerProfileDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /customer-profiles} : get all the Customer Profiles.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Customer Profiles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<CustomerProfileDTO>>> getAllCustomerProfiles(
        CustomerProfileCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get CustomerProfiles by criteria: {}", criteria);
        return customerProfileService
            .countByCriteria(criteria)
            .zipWith(customerProfileService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /customer-profiles/count} : count all the customerProfiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countCustomerProfiles(CustomerProfileCriteria criteria) {
        LOG.debug("REST request to count CustomerProfiles by criteria: {}", criteria);
        return customerProfileService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /customer-profiles/:id} : get the "id" customerProfile.
     *
     * @param id the id of the customerProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerProfileDTO>> getCustomerProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CustomerProfile : {}", id);
        Mono<CustomerProfileDTO> customerProfileDTO = customerProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerProfileDTO);
    }

    /**
     * {@code DELETE  /customer-profiles/:id} : delete the "id" customerProfile.
     *
     * @param id the id of the customerProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomerProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CustomerProfile : {}", id);
        return customerProfileService
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
