package pl.edu.bikerental.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.bikerental.domain.CustomerProfile;
import pl.edu.bikerental.domain.criteria.CustomerProfileCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the CustomerProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerProfileRepository extends ReactiveCrudRepository<CustomerProfile, Long>, CustomerProfileRepositoryInternal {
    Flux<CustomerProfile> findAllBy(Pageable pageable);

    @Query("SELECT * FROM customer_profile entity WHERE entity.customer_id = :id")
    Flux<CustomerProfile> findByCustomer(Long id);

    @Query("SELECT * FROM customer_profile entity WHERE entity.customer_id IS NULL")
    Flux<CustomerProfile> findAllWhereCustomerIsNull();

    @Override
    <S extends CustomerProfile> Mono<S> save(S entity);

    @Override
    Flux<CustomerProfile> findAll();

    @Override
    Mono<CustomerProfile> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CustomerProfileRepositoryInternal {
    <S extends CustomerProfile> Mono<S> save(S entity);

    Flux<CustomerProfile> findAllBy(Pageable pageable);

    Flux<CustomerProfile> findAll();

    Mono<CustomerProfile> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<CustomerProfile> findAllBy(Pageable pageable, Criteria criteria);
    Flux<CustomerProfile> findByCriteria(CustomerProfileCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(CustomerProfileCriteria criteria);
}
