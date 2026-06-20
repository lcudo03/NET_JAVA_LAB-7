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
import pl.edu.bikerental.domain.CustomerProfile;
import pl.edu.bikerental.domain.criteria.CustomerProfileCriteria;
import pl.edu.bikerental.repository.rowmapper.ColumnConverter;
import pl.edu.bikerental.repository.rowmapper.CustomerProfileRowMapper;
import pl.edu.bikerental.repository.rowmapper.CustomerRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the CustomerProfile entity.
 */
@SuppressWarnings("unused")
class CustomerProfileRepositoryInternalImpl
    extends SimpleR2dbcRepository<CustomerProfile, Long>
    implements CustomerProfileRepositoryInternal
{

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final CustomerProfileRowMapper customerprofileMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("customer_profile", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");

    public CustomerProfileRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        CustomerProfileRowMapper customerprofileMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(CustomerProfile.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.customerprofileMapper = customerprofileMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<CustomerProfile> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<CustomerProfile> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CustomerProfileSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, CustomerProfile.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<CustomerProfile> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<CustomerProfile> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private CustomerProfile process(Row row, RowMetadata metadata) {
        CustomerProfile entity = customerprofileMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends CustomerProfile> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<CustomerProfile> findByCriteria(CustomerProfileCriteria customerProfileCriteria, Pageable page) {
        return createQuery(page, buildConditions(customerProfileCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(CustomerProfileCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(CustomerProfileCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getAddress() != null) {
                builder.buildFilterConditionForField(criteria.getAddress(), entityTable.column("address"));
            }
            if (criteria.getCity() != null) {
                builder.buildFilterConditionForField(criteria.getCity(), entityTable.column("city"));
            }
            if (criteria.getLoyaltyPoints() != null) {
                builder.buildFilterConditionForField(criteria.getLoyaltyPoints(), entityTable.column("loyalty_points"));
            }
            if (criteria.getVerified() != null) {
                builder.buildFilterConditionForField(criteria.getVerified(), entityTable.column("verified"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
