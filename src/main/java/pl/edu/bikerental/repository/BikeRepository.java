package pl.edu.bikerental.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.criteria.BikeCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Bike entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BikeRepository extends ReactiveCrudRepository<Bike, Long>, BikeRepositoryInternal {
    Flux<Bike> findAllBy(Pageable pageable);

    @Override
    Mono<Bike> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Bike> findAllWithEagerRelationships();

    @Override
    Flux<Bike> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM bike entity JOIN rel_bike__categories joinTable ON entity.id = joinTable.categories_id WHERE joinTable.categories_id = :id"
    )
    Flux<Bike> findByCategories(Long id);

    @Override
    <S extends Bike> Mono<S> save(S entity);

    @Override
    Flux<Bike> findAll();

    @Override
    Mono<Bike> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BikeRepositoryInternal {
    <S extends Bike> Mono<S> save(S entity);

    Flux<Bike> findAllBy(Pageable pageable);

    Flux<Bike> findAll();

    Mono<Bike> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Bike> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Bike> findByCriteria(BikeCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(BikeCriteria criteria);

    Mono<Bike> findOneWithEagerRelationships(Long id);

    Flux<Bike> findAllWithEagerRelationships();

    Flux<Bike> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
