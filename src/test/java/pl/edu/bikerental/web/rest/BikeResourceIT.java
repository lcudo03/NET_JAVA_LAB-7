package pl.edu.bikerental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static pl.edu.bikerental.domain.BikeAsserts.*;
import static pl.edu.bikerental.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.edu.bikerental.IntegrationTest;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.enumeration.BikeType;
import pl.edu.bikerental.repository.BikeRepository;
import pl.edu.bikerental.repository.EntityManager;
import pl.edu.bikerental.service.BikeService;
import pl.edu.bikerental.service.dto.BikeDTO;
import pl.edu.bikerental.service.mapper.BikeMapper;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link BikeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BikeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SERIAL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NUMBER = "BBBBBBBBBB";

    private static final BikeType DEFAULT_BIKE_TYPE = BikeType.CITY;
    private static final BikeType UPDATED_BIKE_TYPE = BikeType.MOUNTAIN;

    private static final Float DEFAULT_PRICE_PER_HOUR = 0F;
    private static final Float UPDATED_PRICE_PER_HOUR = 1F;
    private static final Float SMALLER_PRICE_PER_HOUR = 0F - 1F;

    private static final Boolean DEFAULT_AVAILABLE = false;
    private static final Boolean UPDATED_AVAILABLE = true;

    private static final LocalDate DEFAULT_PRODUCTION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PRODUCTION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PRODUCTION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/bikes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BikeRepository bikeRepository;

    @Mock
    private BikeRepository bikeRepositoryMock;

    @Autowired
    private BikeMapper bikeMapper;

    @Mock
    private BikeService bikeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Bike bike;

    private Bike insertedBike;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bike createEntity() {
        return new Bike()
            .name(DEFAULT_NAME)
            .serialNumber(DEFAULT_SERIAL_NUMBER)
            .bikeType(DEFAULT_BIKE_TYPE)
            .pricePerHour(DEFAULT_PRICE_PER_HOUR)
            .available(DEFAULT_AVAILABLE)
            .productionDate(DEFAULT_PRODUCTION_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bike createUpdatedEntity() {
        return new Bike()
            .name(UPDATED_NAME)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .bikeType(UPDATED_BIKE_TYPE)
            .pricePerHour(UPDATED_PRICE_PER_HOUR)
            .available(UPDATED_AVAILABLE)
            .productionDate(UPDATED_PRODUCTION_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_bike__categories").block();
            em.deleteAll(Bike.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        bike = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBike != null) {
            bikeRepository.delete(insertedBike).block();
            insertedBike = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBike() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Bike
        BikeDTO bikeDTO = bikeMapper.toDto(bike);
        var returnedBikeDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BikeDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Bike in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBike = bikeMapper.toEntity(returnedBikeDTO);
        assertBikeUpdatableFieldsEquals(returnedBike, getPersistedBike(returnedBike));

        insertedBike = returnedBike;
    }

    @Test
    void createBikeWithExistingId() throws Exception {
        // Create the Bike with an existing ID
        bike.setId(1L);
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bike.setName(null);

        // Create the Bike, which fails.
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSerialNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bike.setSerialNumber(null);

        // Create the Bike, which fails.
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkBikeTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bike.setBikeType(null);

        // Create the Bike, which fails.
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPricePerHourIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bike.setPricePerHour(null);

        // Create the Bike, which fails.
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bike.setAvailable(null);

        // Create the Bike, which fails.
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllBikes() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList
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
            .value(hasItem(bike.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].serialNumber")
            .value(hasItem(DEFAULT_SERIAL_NUMBER))
            .jsonPath("$.[*].bikeType")
            .value(hasItem(DEFAULT_BIKE_TYPE.toString()))
            .jsonPath("$.[*].pricePerHour")
            .value(hasItem(DEFAULT_PRICE_PER_HOUR.doubleValue()))
            .jsonPath("$.[*].available")
            .value(hasItem(DEFAULT_AVAILABLE))
            .jsonPath("$.[*].productionDate")
            .value(hasItem(DEFAULT_PRODUCTION_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBikesWithEagerRelationshipsIsEnabled() {
        when(bikeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(bikeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBikesWithEagerRelationshipsIsNotEnabled() {
        when(bikeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(bikeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getBike() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get the bike
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, bike.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(bike.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.serialNumber")
            .value(is(DEFAULT_SERIAL_NUMBER))
            .jsonPath("$.bikeType")
            .value(is(DEFAULT_BIKE_TYPE.toString()))
            .jsonPath("$.pricePerHour")
            .value(is(DEFAULT_PRICE_PER_HOUR.doubleValue()))
            .jsonPath("$.available")
            .value(is(DEFAULT_AVAILABLE))
            .jsonPath("$.productionDate")
            .value(is(DEFAULT_PRODUCTION_DATE.toString()));
    }

    @Test
    void getBikesByIdFiltering() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        Long id = bike.getId();

        defaultBikeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBikeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBikeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllBikesByNameIsEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where name equals to
        defaultBikeFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllBikesByNameIsInShouldWork() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where name in
        defaultBikeFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllBikesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where name is not null
        defaultBikeFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllBikesByNameContainsSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where name contains
        defaultBikeFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllBikesByNameNotContainsSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where name does not contain
        defaultBikeFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllBikesBySerialNumberIsEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where serialNumber equals to
        defaultBikeFiltering("serialNumber.equals=" + DEFAULT_SERIAL_NUMBER, "serialNumber.equals=" + UPDATED_SERIAL_NUMBER);
    }

    @Test
    void getAllBikesBySerialNumberIsInShouldWork() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where serialNumber in
        defaultBikeFiltering(
            "serialNumber.in=" + DEFAULT_SERIAL_NUMBER + "," + UPDATED_SERIAL_NUMBER,
            "serialNumber.in=" + UPDATED_SERIAL_NUMBER
        );
    }

    @Test
    void getAllBikesBySerialNumberIsNullOrNotNull() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where serialNumber is not null
        defaultBikeFiltering("serialNumber.specified=true", "serialNumber.specified=false");
    }

    @Test
    void getAllBikesBySerialNumberContainsSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where serialNumber contains
        defaultBikeFiltering("serialNumber.contains=" + DEFAULT_SERIAL_NUMBER, "serialNumber.contains=" + UPDATED_SERIAL_NUMBER);
    }

    @Test
    void getAllBikesBySerialNumberNotContainsSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where serialNumber does not contain
        defaultBikeFiltering(
            "serialNumber.doesNotContain=" + UPDATED_SERIAL_NUMBER,
            "serialNumber.doesNotContain=" + DEFAULT_SERIAL_NUMBER
        );
    }

    @Test
    void getAllBikesByBikeTypeIsEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where bikeType equals to
        defaultBikeFiltering("bikeType.equals=" + DEFAULT_BIKE_TYPE, "bikeType.equals=" + UPDATED_BIKE_TYPE);
    }

    @Test
    void getAllBikesByBikeTypeIsInShouldWork() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where bikeType in
        defaultBikeFiltering("bikeType.in=" + DEFAULT_BIKE_TYPE + "," + UPDATED_BIKE_TYPE, "bikeType.in=" + UPDATED_BIKE_TYPE);
    }

    @Test
    void getAllBikesByBikeTypeIsNullOrNotNull() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where bikeType is not null
        defaultBikeFiltering("bikeType.specified=true", "bikeType.specified=false");
    }

    @Test
    void getAllBikesByPricePerHourIsEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where pricePerHour equals to
        defaultBikeFiltering("pricePerHour.equals=" + DEFAULT_PRICE_PER_HOUR, "pricePerHour.equals=" + UPDATED_PRICE_PER_HOUR);
    }

    @Test
    void getAllBikesByPricePerHourIsInShouldWork() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where pricePerHour in
        defaultBikeFiltering(
            "pricePerHour.in=" + DEFAULT_PRICE_PER_HOUR + "," + UPDATED_PRICE_PER_HOUR,
            "pricePerHour.in=" + UPDATED_PRICE_PER_HOUR
        );
    }

    @Test
    void getAllBikesByPricePerHourIsNullOrNotNull() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where pricePerHour is not null
        defaultBikeFiltering("pricePerHour.specified=true", "pricePerHour.specified=false");
    }

    @Test
    void getAllBikesByPricePerHourIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where pricePerHour is greater than or equal to
        defaultBikeFiltering(
            "pricePerHour.greaterThanOrEqual=" + DEFAULT_PRICE_PER_HOUR,
            "pricePerHour.greaterThanOrEqual=" + UPDATED_PRICE_PER_HOUR
        );
    }

    @Test
    void getAllBikesByPricePerHourIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where pricePerHour is less than or equal to
        defaultBikeFiltering(
            "pricePerHour.lessThanOrEqual=" + DEFAULT_PRICE_PER_HOUR,
            "pricePerHour.lessThanOrEqual=" + SMALLER_PRICE_PER_HOUR
        );
    }

    @Test
    void getAllBikesByPricePerHourIsLessThanSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where pricePerHour is less than
        defaultBikeFiltering("pricePerHour.lessThan=" + UPDATED_PRICE_PER_HOUR, "pricePerHour.lessThan=" + DEFAULT_PRICE_PER_HOUR);
    }

    @Test
    void getAllBikesByPricePerHourIsGreaterThanSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where pricePerHour is greater than
        defaultBikeFiltering("pricePerHour.greaterThan=" + SMALLER_PRICE_PER_HOUR, "pricePerHour.greaterThan=" + DEFAULT_PRICE_PER_HOUR);
    }

    @Test
    void getAllBikesByAvailableIsEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where available equals to
        defaultBikeFiltering("available.equals=" + DEFAULT_AVAILABLE, "available.equals=" + UPDATED_AVAILABLE);
    }

    @Test
    void getAllBikesByAvailableIsInShouldWork() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where available in
        defaultBikeFiltering("available.in=" + DEFAULT_AVAILABLE + "," + UPDATED_AVAILABLE, "available.in=" + UPDATED_AVAILABLE);
    }

    @Test
    void getAllBikesByAvailableIsNullOrNotNull() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where available is not null
        defaultBikeFiltering("available.specified=true", "available.specified=false");
    }

    @Test
    void getAllBikesByProductionDateIsEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where productionDate equals to
        defaultBikeFiltering("productionDate.equals=" + DEFAULT_PRODUCTION_DATE, "productionDate.equals=" + UPDATED_PRODUCTION_DATE);
    }

    @Test
    void getAllBikesByProductionDateIsInShouldWork() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where productionDate in
        defaultBikeFiltering(
            "productionDate.in=" + DEFAULT_PRODUCTION_DATE + "," + UPDATED_PRODUCTION_DATE,
            "productionDate.in=" + UPDATED_PRODUCTION_DATE
        );
    }

    @Test
    void getAllBikesByProductionDateIsNullOrNotNull() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where productionDate is not null
        defaultBikeFiltering("productionDate.specified=true", "productionDate.specified=false");
    }

    @Test
    void getAllBikesByProductionDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where productionDate is greater than or equal to
        defaultBikeFiltering(
            "productionDate.greaterThanOrEqual=" + DEFAULT_PRODUCTION_DATE,
            "productionDate.greaterThanOrEqual=" + UPDATED_PRODUCTION_DATE
        );
    }

    @Test
    void getAllBikesByProductionDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where productionDate is less than or equal to
        defaultBikeFiltering(
            "productionDate.lessThanOrEqual=" + DEFAULT_PRODUCTION_DATE,
            "productionDate.lessThanOrEqual=" + SMALLER_PRODUCTION_DATE
        );
    }

    @Test
    void getAllBikesByProductionDateIsLessThanSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where productionDate is less than
        defaultBikeFiltering("productionDate.lessThan=" + UPDATED_PRODUCTION_DATE, "productionDate.lessThan=" + DEFAULT_PRODUCTION_DATE);
    }

    @Test
    void getAllBikesByProductionDateIsGreaterThanSomething() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        // Get all the bikeList where productionDate is greater than
        defaultBikeFiltering(
            "productionDate.greaterThan=" + SMALLER_PRODUCTION_DATE,
            "productionDate.greaterThan=" + DEFAULT_PRODUCTION_DATE
        );
    }

    private void defaultBikeFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultBikeShouldBeFound(shouldBeFound);
        defaultBikeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBikeShouldBeFound(String filter) {
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
            .value(hasItem(bike.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))

            .jsonPath("$.[*].serialNumber")
            .value(hasItem(DEFAULT_SERIAL_NUMBER))

            .jsonPath("$.[*].bikeType")
            .value(hasItem(DEFAULT_BIKE_TYPE.toString()))

            .jsonPath("$.[*].pricePerHour")
            .value(hasItem(DEFAULT_PRICE_PER_HOUR.doubleValue()))

            .jsonPath("$.[*].available")
            .value(hasItem(DEFAULT_AVAILABLE))

            .jsonPath("$.[*].productionDate")
            .value(hasItem(DEFAULT_PRODUCTION_DATE.toString()));

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
    private void defaultBikeShouldNotBeFound(String filter) {
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
    void getNonExistingBike() {
        // Get the bike
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBike() throws Exception {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bike
        Bike updatedBike = bikeRepository.findById(bike.getId()).block();
        updatedBike
            .name(UPDATED_NAME)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .bikeType(UPDATED_BIKE_TYPE)
            .pricePerHour(UPDATED_PRICE_PER_HOUR)
            .available(UPDATED_AVAILABLE)
            .productionDate(UPDATED_PRODUCTION_DATE);
        BikeDTO bikeDTO = bikeMapper.toDto(updatedBike);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bikeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBikeToMatchAllProperties(updatedBike);
    }

    @Test
    void putNonExistingBike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bike.setId(longCount.incrementAndGet());

        // Create the Bike
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bikeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bike.setId(longCount.incrementAndGet());

        // Create the Bike
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bike.setId(longCount.incrementAndGet());

        // Create the Bike
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBikeWithPatch() throws Exception {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bike using partial update
        Bike partialUpdatedBike = new Bike();
        partialUpdatedBike.setId(bike.getId());

        partialUpdatedBike.pricePerHour(UPDATED_PRICE_PER_HOUR).productionDate(UPDATED_PRODUCTION_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBike.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBike))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bike in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBikeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBike, bike), getPersistedBike(bike));
    }

    @Test
    void fullUpdateBikeWithPatch() throws Exception {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bike using partial update
        Bike partialUpdatedBike = new Bike();
        partialUpdatedBike.setId(bike.getId());

        partialUpdatedBike
            .name(UPDATED_NAME)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .bikeType(UPDATED_BIKE_TYPE)
            .pricePerHour(UPDATED_PRICE_PER_HOUR)
            .available(UPDATED_AVAILABLE)
            .productionDate(UPDATED_PRODUCTION_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBike.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBike))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bike in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBikeUpdatableFieldsEquals(partialUpdatedBike, getPersistedBike(partialUpdatedBike));
    }

    @Test
    void patchNonExistingBike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bike.setId(longCount.incrementAndGet());

        // Create the Bike
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, bikeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bike.setId(longCount.incrementAndGet());

        // Create the Bike
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bike.setId(longCount.incrementAndGet());

        // Create the Bike
        BikeDTO bikeDTO = bikeMapper.toDto(bike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bikeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBike() {
        // Initialize the database
        insertedBike = bikeRepository.save(bike).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bike
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, bike.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bikeRepository.count().block();
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

    protected Bike getPersistedBike(Bike bike) {
        return bikeRepository.findById(bike.getId()).block();
    }

    protected void assertPersistedBikeToMatchAllProperties(Bike expectedBike) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBikeAllPropertiesEquals(expectedBike, getPersistedBike(expectedBike));
        assertBikeUpdatableFieldsEquals(expectedBike, getPersistedBike(expectedBike));
    }

    protected void assertPersistedBikeToMatchUpdatableProperties(Bike expectedBike) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBikeAllUpdatablePropertiesEquals(expectedBike, getPersistedBike(expectedBike));
        assertBikeUpdatableFieldsEquals(expectedBike, getPersistedBike(expectedBike));
    }
}
