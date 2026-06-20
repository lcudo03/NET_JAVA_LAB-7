package pl.edu.bikerental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static pl.edu.bikerental.domain.RentalAsserts.*;
import static pl.edu.bikerental.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.edu.bikerental.IntegrationTest;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.Customer;
import pl.edu.bikerental.domain.Rental;
import pl.edu.bikerental.domain.enumeration.RentalStatus;
import pl.edu.bikerental.repository.BikeRepository;
import pl.edu.bikerental.repository.CustomerRepository;
import pl.edu.bikerental.repository.EntityManager;
import pl.edu.bikerental.repository.RentalRepository;
import pl.edu.bikerental.service.dto.RentalDTO;
import pl.edu.bikerental.service.mapper.RentalMapper;

/**
 * Integration tests for the {@link RentalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RentalResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final Float DEFAULT_TOTAL_PRICE = 0F;
    private static final Float UPDATED_TOTAL_PRICE = 1F;
    private static final Float SMALLER_TOTAL_PRICE = 0F - 1F;

    private static final RentalStatus DEFAULT_STATUS = RentalStatus.ACTIVE;
    private static final RentalStatus UPDATED_STATUS = RentalStatus.FINISHED;

    private static final String ENTITY_API_URL = "/api/rentals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalMapper rentalMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Rental rental;

    private Rental insertedRental;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BikeRepository bikeRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rental createEntity(EntityManager em) {
        Rental rental = new Rental()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .totalPrice(DEFAULT_TOTAL_PRICE)
            .status(DEFAULT_STATUS);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createEntity()).block();
        rental.setCustomer(customer);
        // Add required entity
        Bike bike;
        bike = em.insert(BikeResourceIT.createEntity()).block();
        rental.setBike(bike);
        return rental;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rental createUpdatedEntity(EntityManager em) {
        Rental updatedRental = new Rental()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .status(UPDATED_STATUS);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createUpdatedEntity()).block();
        updatedRental.setCustomer(customer);
        // Add required entity
        Bike bike;
        bike = em.insert(BikeResourceIT.createUpdatedEntity()).block();
        updatedRental.setBike(bike);
        return updatedRental;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Rental.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        CustomerResourceIT.deleteEntities(em);
        BikeResourceIT.deleteEntities(em);
    }

    @BeforeEach
    void initTest() {
        rental = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRental != null) {
            rentalRepository.delete(insertedRental).block();
            insertedRental = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRental() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);
        var returnedRentalDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RentalDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Rental in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRental = rentalMapper.toEntity(returnedRentalDTO);
        assertRentalUpdatableFieldsEquals(returnedRental, getPersistedRental(returnedRental));

        insertedRental = returnedRental;
    }

    @Test
    void createRentalWithExistingId() throws Exception {
        // Create the Rental with an existing ID
        rental.setId(1L);
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rental.setStartDate(null);

        // Create the Rental, which fails.
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rental.setStatus(null);

        // Create the Rental, which fails.
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRentals() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rental.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(DEFAULT_TOTAL_PRICE.doubleValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getRental() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get the rental
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rental.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rental.getId().intValue()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.totalPrice")
            .value(is(DEFAULT_TOTAL_PRICE.doubleValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getRentalsByIdFiltering() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        Long id = rental.getId();

        defaultRentalFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRentalFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRentalFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllRentalsByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where startDate equals to
        defaultRentalFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllRentalsByStartDateIsInShouldWork() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where startDate in
        defaultRentalFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    void getAllRentalsByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where startDate is not null
        defaultRentalFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllRentalsByStartDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where startDate is greater than or equal to
        defaultRentalFiltering("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE, "startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    void getAllRentalsByStartDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where startDate is less than or equal to
        defaultRentalFiltering("startDate.lessThanOrEqual=" + DEFAULT_START_DATE, "startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    void getAllRentalsByStartDateIsLessThanSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where startDate is less than
        defaultRentalFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllRentalsByStartDateIsGreaterThanSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where startDate is greater than
        defaultRentalFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllRentalsByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where endDate equals to
        defaultRentalFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllRentalsByEndDateIsInShouldWork() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where endDate in
        defaultRentalFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllRentalsByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where endDate is not null
        defaultRentalFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllRentalsByEndDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where endDate is greater than or equal to
        defaultRentalFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    void getAllRentalsByEndDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where endDate is less than or equal to
        defaultRentalFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    void getAllRentalsByEndDateIsLessThanSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where endDate is less than
        defaultRentalFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllRentalsByEndDateIsGreaterThanSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where endDate is greater than
        defaultRentalFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllRentalsByTotalPriceIsEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where totalPrice equals to
        defaultRentalFiltering("totalPrice.equals=" + DEFAULT_TOTAL_PRICE, "totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    void getAllRentalsByTotalPriceIsInShouldWork() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where totalPrice in
        defaultRentalFiltering("totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE, "totalPrice.in=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    void getAllRentalsByTotalPriceIsNullOrNotNull() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where totalPrice is not null
        defaultRentalFiltering("totalPrice.specified=true", "totalPrice.specified=false");
    }

    @Test
    void getAllRentalsByTotalPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where totalPrice is greater than or equal to
        defaultRentalFiltering(
            "totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    void getAllRentalsByTotalPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where totalPrice is less than or equal to
        defaultRentalFiltering("totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE, "totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    void getAllRentalsByTotalPriceIsLessThanSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where totalPrice is less than
        defaultRentalFiltering("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE, "totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllRentalsByTotalPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where totalPrice is greater than
        defaultRentalFiltering("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE, "totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllRentalsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where status equals to
        defaultRentalFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllRentalsByStatusIsInShouldWork() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where status in
        defaultRentalFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllRentalsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        // Get all the rentalList where status is not null
        defaultRentalFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllRentalsByCustomerIsEqualToSomething() {
        Customer customer = CustomerResourceIT.createEntity();
        customerRepository.save(customer).block();
        Long customerId = customer.getId();
        rental.setCustomerId(customerId);
        insertedRental = rentalRepository.save(rental).block();
        // Get all the rentalList where customer equals to customerId
        defaultRentalShouldBeFound("customerId.equals=" + customerId);

        // Get all the rentalList where customer equals to (customerId + 1)
        defaultRentalShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    @Test
    void getAllRentalsByBikeIsEqualToSomething() {
        Bike bike = BikeResourceIT.createEntity();
        bikeRepository.save(bike).block();
        Long bikeId = bike.getId();
        rental.setBikeId(bikeId);
        insertedRental = rentalRepository.save(rental).block();
        // Get all the rentalList where bike equals to bikeId
        defaultRentalShouldBeFound("bikeId.equals=" + bikeId);

        // Get all the rentalList where bike equals to (bikeId + 1)
        defaultRentalShouldNotBeFound("bikeId.equals=" + (bikeId + 1));
    }

    private void defaultRentalFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultRentalShouldBeFound(shouldBeFound);
        defaultRentalShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRentalShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rental.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))

            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))

            .jsonPath("$.[*].totalPrice")
            .value(hasItem(DEFAULT_TOTAL_PRICE.doubleValue()))

            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRentalShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingRental() {
        // Get the rental
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRental() throws Exception {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rental
        Rental updatedRental = rentalRepository.findById(rental.getId()).block();
        updatedRental.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).totalPrice(UPDATED_TOTAL_PRICE).status(UPDATED_STATUS);
        RentalDTO rentalDTO = rentalMapper.toDto(updatedRental);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rentalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRentalToMatchAllProperties(updatedRental);
    }

    @Test
    void putNonExistingRental() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rental.setId(longCount.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rentalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRental() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rental.setId(longCount.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRental() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rental.setId(longCount.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRentalWithPatch() throws Exception {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rental using partial update
        Rental partialUpdatedRental = new Rental();
        partialUpdatedRental.setId(rental.getId());

        partialUpdatedRental.endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRental.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRental))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rental in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRentalUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRental, rental), getPersistedRental(rental));
    }

    @Test
    void fullUpdateRentalWithPatch() throws Exception {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rental using partial update
        Rental partialUpdatedRental = new Rental();
        partialUpdatedRental.setId(rental.getId());

        partialUpdatedRental.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).totalPrice(UPDATED_TOTAL_PRICE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRental.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRental))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rental in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRentalUpdatableFieldsEquals(partialUpdatedRental, getPersistedRental(partialUpdatedRental));
    }

    @Test
    void patchNonExistingRental() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rental.setId(longCount.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rentalDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRental() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rental.setId(longCount.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRental() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rental.setId(longCount.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rental in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRental() {
        // Initialize the database
        insertedRental = rentalRepository.save(rental).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rental
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rental.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rentalRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Rental getPersistedRental(Rental rental) {
        return rentalRepository.findById(rental.getId()).block();
    }

    protected void assertPersistedRentalToMatchAllProperties(Rental expectedRental) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRentalAllPropertiesEquals(expectedRental, getPersistedRental(expectedRental));
        assertRentalUpdatableFieldsEquals(expectedRental, getPersistedRental(expectedRental));
    }

    protected void assertPersistedRentalToMatchUpdatableProperties(Rental expectedRental) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRentalAllUpdatablePropertiesEquals(expectedRental, getPersistedRental(expectedRental));
        assertRentalUpdatableFieldsEquals(expectedRental, getPersistedRental(expectedRental));
    }
}
