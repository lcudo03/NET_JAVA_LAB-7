package pl.edu.bikerental.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.Category;
import pl.edu.bikerental.domain.criteria.BikeCriteria;
import pl.edu.bikerental.repository.rowmapper.BikeRowMapper;
import pl.edu.bikerental.repository.rowmapper.ColumnConverter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Bike entity.
 */
@SuppressWarnings("unused")
class BikeRepositoryInternalImpl extends SimpleR2dbcRepository<Bike, Long> implements BikeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BikeRowMapper bikeMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("bike", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable categoriesLink = new EntityManager.LinkTable(
        "rel_bike__categories",
        "bike_id",
        "categories_id"
    );

    public BikeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BikeRowMapper bikeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Bike.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.bikeMapper = bikeMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Bike> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Bike> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BikeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Bike.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Bike> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Bike> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Bike> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Bike> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Bike> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Bike process(Row row, RowMetadata metadata) {
        Bike entity = bikeMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Bike> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Bike> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(categoriesLink, entity.getId(), entity.getCategorieses().stream().map(Category::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(categoriesLink, entityId);
    }

    @Override
    public Flux<Bike> findByCriteria(BikeCriteria bikeCriteria, Pageable page) {
        return createQuery(page, buildConditions(bikeCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(BikeCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(BikeCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getSerialNumber() != null) {
                builder.buildFilterConditionForField(criteria.getSerialNumber(), entityTable.column("serial_number"));
            }
            if (criteria.getBikeType() != null) {
                builder.buildFilterConditionForField(criteria.getBikeType(), entityTable.column("bike_type"));
            }
            if (criteria.getPricePerHour() != null) {
                builder.buildFilterConditionForField(criteria.getPricePerHour(), entityTable.column("price_per_hour"));
            }
            if (criteria.getAvailable() != null) {
                builder.buildFilterConditionForField(criteria.getAvailable(), entityTable.column("available"));
            }
            if (criteria.getProductionDate() != null) {
                builder.buildFilterConditionForField(criteria.getProductionDate(), entityTable.column("production_date"));
            }
        }
        return builder.buildConditions();
    }
}
