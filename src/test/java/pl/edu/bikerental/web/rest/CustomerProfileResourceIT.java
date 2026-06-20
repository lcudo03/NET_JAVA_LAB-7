package pl.edu.bikerental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static pl.edu.bikerental.domain.CustomerProfileAsserts.*;
import static pl.edu.bikerental.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.edu.bikerental.domain.Customer;
import pl.edu.bikerental.domain.CustomerProfile;
import pl.edu.bikerental.repository.CustomerProfileRepository;
import pl.edu.bikerental.repository.CustomerRepository;
import pl.edu.bikerental.repository.EntityManager;
import pl.edu.bikerental.service.dto.CustomerProfileDTO;
import pl.edu.bikerental.service.mapper.CustomerProfileMapper;

/**
 * Integration tests for the {@link CustomerProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CustomerProfileResourceIT {

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final Integer DEFAULT_LOYALTY_POINTS = 0;
    private static final Integer UPDATED_LOYALTY_POINTS = 1;
    private static final Integer SMALLER_LOYALTY_POINTS = 0 - 1;

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final String ENTITY_API_URL = "/api/customer-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private CustomerProfileMapper customerProfileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CustomerProfile customerProfile;

    private CustomerProfile insertedCustomerProfile;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerProfile createEntity(EntityManager em) {
        CustomerProfile customerProfile = new CustomerProfile()
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .loyaltyPoints(DEFAULT_LOYALTY_POINTS)
            .verified(DEFAULT_VERIFIED);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createEntity()).block();
        customerProfile.setCustomer(customer);
        return customerProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerProfile createUpdatedEntity(EntityManager em) {
        CustomerProfile updatedCustomerProfile = new CustomerProfile()
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .verified(UPDATED_VERIFIED);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createUpdatedEntity()).block();
        updatedCustomerProfile.setCustomer(customer);
        return updatedCustomerProfile;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CustomerProfile.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        CustomerResourceIT.deleteEntities(em);
    }

    @BeforeEach
    void initTest() {
        customerProfile = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomerProfile != null) {
            customerProfileRepository.delete(insertedCustomerProfile).block();
            insertedCustomerProfile = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCustomerProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);
        var returnedCustomerProfileDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CustomerProfileDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the CustomerProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomerProfile = customerProfileMapper.toEntity(returnedCustomerProfileDTO);
        assertCustomerProfileUpdatableFieldsEquals(returnedCustomerProfile, getPersistedCustomerProfile(returnedCustomerProfile));

        insertedCustomerProfile = returnedCustomerProfile;
    }

    @Test
    void createCustomerProfileWithExistingId() throws Exception {
        // Create the CustomerProfile with an existing ID
        customerProfile.setId(1L);
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerProfile.setAddress(null);

        // Create the CustomerProfile, which fails.
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerProfile.setCity(null);

        // Create the CustomerProfile, which fails.
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkVerifiedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerProfile.setVerified(null);

        // Create the CustomerProfile, which fails.
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCustomerProfiles() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList
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
            .value(hasItem(customerProfile.getId().intValue()))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].loyaltyPoints")
            .value(hasItem(DEFAULT_LOYALTY_POINTS))
            .jsonPath("$.[*].verified")
            .value(hasItem(DEFAULT_VERIFIED));
    }

    @Test
    void getCustomerProfile() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get the customerProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, customerProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(customerProfile.getId().intValue()))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.loyaltyPoints")
            .value(is(DEFAULT_LOYALTY_POINTS))
            .jsonPath("$.verified")
            .value(is(DEFAULT_VERIFIED));
    }

    @Test
    void getCustomerProfilesByIdFiltering() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        Long id = customerProfile.getId();

        defaultCustomerProfileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCustomerProfileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCustomerProfileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCustomerProfilesByAddressIsEqualToSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where address equals to
        defaultCustomerProfileFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllCustomerProfilesByAddressIsInShouldWork() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where address in
        defaultCustomerProfileFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllCustomerProfilesByAddressIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where address is not null
        defaultCustomerProfileFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    void getAllCustomerProfilesByAddressContainsSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where address contains
        defaultCustomerProfileFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllCustomerProfilesByAddressNotContainsSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where address does not contain
        defaultCustomerProfileFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    void getAllCustomerProfilesByCityIsEqualToSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where city equals to
        defaultCustomerProfileFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    void getAllCustomerProfilesByCityIsInShouldWork() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where city in
        defaultCustomerProfileFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    void getAllCustomerProfilesByCityIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where city is not null
        defaultCustomerProfileFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    void getAllCustomerProfilesByCityContainsSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where city contains
        defaultCustomerProfileFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    void getAllCustomerProfilesByCityNotContainsSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where city does not contain
        defaultCustomerProfileFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    void getAllCustomerProfilesByLoyaltyPointsIsEqualToSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where loyaltyPoints equals to
        defaultCustomerProfileFiltering("loyaltyPoints.equals=" + DEFAULT_LOYALTY_POINTS, "loyaltyPoints.equals=" + UPDATED_LOYALTY_POINTS);
    }

    @Test
    void getAllCustomerProfilesByLoyaltyPointsIsInShouldWork() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where loyaltyPoints in
        defaultCustomerProfileFiltering(
            "loyaltyPoints.in=" + DEFAULT_LOYALTY_POINTS + "," + UPDATED_LOYALTY_POINTS,
            "loyaltyPoints.in=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    void getAllCustomerProfilesByLoyaltyPointsIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where loyaltyPoints is not null
        defaultCustomerProfileFiltering("loyaltyPoints.specified=true", "loyaltyPoints.specified=false");
    }

    @Test
    void getAllCustomerProfilesByLoyaltyPointsIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where loyaltyPoints is greater than or equal to
        defaultCustomerProfileFiltering(
            "loyaltyPoints.greaterThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.greaterThanOrEqual=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    void getAllCustomerProfilesByLoyaltyPointsIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where loyaltyPoints is less than or equal to
        defaultCustomerProfileFiltering(
            "loyaltyPoints.lessThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.lessThanOrEqual=" + SMALLER_LOYALTY_POINTS
        );
    }

    @Test
    void getAllCustomerProfilesByLoyaltyPointsIsLessThanSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where loyaltyPoints is less than
        defaultCustomerProfileFiltering(
            "loyaltyPoints.lessThan=" + UPDATED_LOYALTY_POINTS,
            "loyaltyPoints.lessThan=" + DEFAULT_LOYALTY_POINTS
        );
    }

    @Test
    void getAllCustomerProfilesByLoyaltyPointsIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where loyaltyPoints is greater than
        defaultCustomerProfileFiltering(
            "loyaltyPoints.greaterThan=" + SMALLER_LOYALTY_POINTS,
            "loyaltyPoints.greaterThan=" + DEFAULT_LOYALTY_POINTS
        );
    }

    @Test
    void getAllCustomerProfilesByVerifiedIsEqualToSomething() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where verified equals to
        defaultCustomerProfileFiltering("verified.equals=" + DEFAULT_VERIFIED, "verified.equals=" + UPDATED_VERIFIED);
    }

    @Test
    void getAllCustomerProfilesByVerifiedIsInShouldWork() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where verified in
        defaultCustomerProfileFiltering("verified.in=" + DEFAULT_VERIFIED + "," + UPDATED_VERIFIED, "verified.in=" + UPDATED_VERIFIED);
    }

    @Test
    void getAllCustomerProfilesByVerifiedIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList where verified is not null
        defaultCustomerProfileFiltering("verified.specified=true", "verified.specified=false");
    }

    @Test
    void getAllCustomerProfilesByCustomerIsEqualToSomething() {
        // Get already existing entity
        Customer customer = customerProfile.getCustomer();
        Long customerId = customer.getId();

        // Get all the customerProfileList where customer equals to customerId
        defaultCustomerProfileShouldBeFound("customerId.equals=" + customerId);

        // Get all the customerProfileList where customer equals to (customerId + 1)
        defaultCustomerProfileShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultCustomerProfileFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCustomerProfileShouldBeFound(shouldBeFound);
        defaultCustomerProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerProfileShouldBeFound(String filter) {
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
            .value(hasItem(customerProfile.getId().intValue()))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))

            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))

            .jsonPath("$.[*].loyaltyPoints")
            .value(hasItem(DEFAULT_LOYALTY_POINTS))

            .jsonPath("$.[*].verified")
            .value(hasItem(DEFAULT_VERIFIED));

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
    private void defaultCustomerProfileShouldNotBeFound(String filter) {
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
    void getNonExistingCustomerProfile() {
        // Get the customerProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCustomerProfile() throws Exception {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerProfile
        CustomerProfile updatedCustomerProfile = customerProfileRepository.findById(customerProfile.getId()).block();
        updatedCustomerProfile.address(UPDATED_ADDRESS).city(UPDATED_CITY).loyaltyPoints(UPDATED_LOYALTY_POINTS).verified(UPDATED_VERIFIED);
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(updatedCustomerProfile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerProfileToMatchAllProperties(updatedCustomerProfile);
    }

    @Test
    void putNonExistingCustomerProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerProfile.setId(longCount.incrementAndGet());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomerProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerProfile.setId(longCount.incrementAndGet());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomerProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerProfile.setId(longCount.incrementAndGet());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerProfileWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerProfile using partial update
        CustomerProfile partialUpdatedCustomerProfile = new CustomerProfile();
        partialUpdatedCustomerProfile.setId(customerProfile.getId());

        partialUpdatedCustomerProfile.verified(UPDATED_VERIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomerProfile, customerProfile),
            getPersistedCustomerProfile(customerProfile)
        );
    }

    @Test
    void fullUpdateCustomerProfileWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerProfile using partial update
        CustomerProfile partialUpdatedCustomerProfile = new CustomerProfile();
        partialUpdatedCustomerProfile.setId(customerProfile.getId());

        partialUpdatedCustomerProfile
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .verified(UPDATED_VERIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerProfileUpdatableFieldsEquals(
            partialUpdatedCustomerProfile,
            getPersistedCustomerProfile(partialUpdatedCustomerProfile)
        );
    }

    @Test
    void patchNonExistingCustomerProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerProfile.setId(longCount.incrementAndGet());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, customerProfileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomerProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerProfile.setId(longCount.incrementAndGet());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomerProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerProfile.setId(longCount.incrementAndGet());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomerProfile() {
        // Initialize the database
        insertedCustomerProfile = customerProfileRepository.save(customerProfile).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customerProfile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, customerProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerProfileRepository.count().block();
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

    protected CustomerProfile getPersistedCustomerProfile(CustomerProfile customerProfile) {
        return customerProfileRepository.findById(customerProfile.getId()).block();
    }

    protected void assertPersistedCustomerProfileToMatchAllProperties(CustomerProfile expectedCustomerProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerProfileAllPropertiesEquals(expectedCustomerProfile, getPersistedCustomerProfile(expectedCustomerProfile));
        assertCustomerProfileUpdatableFieldsEquals(expectedCustomerProfile, getPersistedCustomerProfile(expectedCustomerProfile));
    }

    protected void assertPersistedCustomerProfileToMatchUpdatableProperties(CustomerProfile expectedCustomerProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerProfileAllUpdatablePropertiesEquals(expectedCustomerProfile, getPersistedCustomerProfile(expectedCustomerProfile));
        assertCustomerProfileUpdatableFieldsEquals(expectedCustomerProfile, getPersistedCustomerProfile(expectedCustomerProfile));
    }
}
