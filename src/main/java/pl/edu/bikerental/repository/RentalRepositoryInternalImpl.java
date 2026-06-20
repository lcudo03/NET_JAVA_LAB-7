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
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import pl.edu.bikerental.domain.Rental;
import pl.edu.bikerental.domain.criteria.RentalCriteria;
import pl.edu.bikerental.repository.rowmapper.BikeRowMapper;
import pl.edu.bikerental.repository.rowmapper.ColumnConverter;
import pl.edu.bikerental.repository.rowmapper.CustomerRowMapper;
import pl.edu.bikerental.repository.rowmapper.RentalRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Rental entity.
 */
@SuppressWarnings("unused")
class RentalRepositoryInternalImpl extends SimpleR2dbcRepository<Rental, Long> implements RentalRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final BikeRowMapper bikeMapper;
    private final RentalRowMapper rentalMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("rental", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");
    private static final Table bikeTable = Table.aliased("bike", "bike");

    public RentalRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        BikeRowMapper bikeMapper,
        RentalRowMapper rentalMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Rental.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.bikeMapper = bikeMapper;
        this.rentalMapper = rentalMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Rental> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Rental> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RentalSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        columns.addAll(BikeSqlHelper.getColumns(bikeTable, "bike"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable))
            .leftOuterJoin(bikeTable)
            .on(Column.create("bike_id", entityTable))
            .equals(Column.create("id", bikeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Rental.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Rental> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Rental> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Rental process(Row row, RowMetadata metadata) {
        Rental entity = rentalMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        entity.setBike(bikeMapper.apply(row, "bike"));
        return entity;
    }

    @Override
    public <S extends Rental> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Rental> findByCriteria(RentalCriteria rentalCriteria, Pageable page) {
        return createQuery(page, buildConditions(rentalCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(RentalCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(RentalCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
            if (criteria.getTotalPrice() != null) {
                builder.buildFilterConditionForField(criteria.getTotalPrice(), entityTable.column("total_price"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
            if (criteria.getBikeId() != null) {
                builder.buildFilterConditionForField(criteria.getBikeId(), bikeTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
