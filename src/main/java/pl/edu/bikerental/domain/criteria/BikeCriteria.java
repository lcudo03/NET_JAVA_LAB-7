package pl.edu.bikerental.domain.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import pl.edu.bikerental.domain.enumeration.BikeType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link pl.edu.bikerental.domain.Bike} entity. This class is used
 * in {@link pl.edu.bikerental.web.rest.BikeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bikes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BikeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering BikeType
     */
    public static class BikeTypeFilter extends Filter<BikeType> {

        public BikeTypeFilter() {}

        public BikeTypeFilter(BikeTypeFilter filter) {
            super(filter);
        }

        @Override
        public BikeTypeFilter copy() {
            return new BikeTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter serialNumber;

    private BikeTypeFilter bikeType;

    private FloatFilter pricePerHour;

    private BooleanFilter available;

    private LocalDateFilter productionDate;

    private Boolean distinct;

    public BikeCriteria() {}

    public BikeCriteria(BikeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.serialNumber = other.optionalSerialNumber().map(StringFilter::copy).orElse(null);
        this.bikeType = other.optionalBikeType().map(BikeTypeFilter::copy).orElse(null);
        this.pricePerHour = other.optionalPricePerHour().map(FloatFilter::copy).orElse(null);
        this.available = other.optionalAvailable().map(BooleanFilter::copy).orElse(null);
        this.productionDate = other.optionalProductionDate().map(LocalDateFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BikeCriteria copy() {
        return new BikeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSerialNumber() {
        return serialNumber;
    }

    public Optional<StringFilter> optionalSerialNumber() {
        return Optional.ofNullable(serialNumber);
    }

    public StringFilter serialNumber() {
        if (serialNumber == null) {
            setSerialNumber(new StringFilter());
        }
        return serialNumber;
    }

    public void setSerialNumber(StringFilter serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BikeTypeFilter getBikeType() {
        return bikeType;
    }

    public Optional<BikeTypeFilter> optionalBikeType() {
        return Optional.ofNullable(bikeType);
    }

    public BikeTypeFilter bikeType() {
        if (bikeType == null) {
            setBikeType(new BikeTypeFilter());
        }
        return bikeType;
    }

    public void setBikeType(BikeTypeFilter bikeType) {
        this.bikeType = bikeType;
    }

    public FloatFilter getPricePerHour() {
        return pricePerHour;
    }

    public Optional<FloatFilter> optionalPricePerHour() {
        return Optional.ofNullable(pricePerHour);
    }

    public FloatFilter pricePerHour() {
        if (pricePerHour == null) {
            setPricePerHour(new FloatFilter());
        }
        return pricePerHour;
    }

    public void setPricePerHour(FloatFilter pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public BooleanFilter getAvailable() {
        return available;
    }

    public Optional<BooleanFilter> optionalAvailable() {
        return Optional.ofNullable(available);
    }

    public BooleanFilter available() {
        if (available == null) {
            setAvailable(new BooleanFilter());
        }
        return available;
    }

    public void setAvailable(BooleanFilter available) {
        this.available = available;
    }

    public LocalDateFilter getProductionDate() {
        return productionDate;
    }

    public Optional<LocalDateFilter> optionalProductionDate() {
        return Optional.ofNullable(productionDate);
    }

    public LocalDateFilter productionDate() {
        if (productionDate == null) {
            setProductionDate(new LocalDateFilter());
        }
        return productionDate;
    }

    public void setProductionDate(LocalDateFilter productionDate) {
        this.productionDate = productionDate;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BikeCriteria that = (BikeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(serialNumber, that.serialNumber) &&
            Objects.equals(bikeType, that.bikeType) &&
            Objects.equals(pricePerHour, that.pricePerHour) &&
            Objects.equals(available, that.available) &&
            Objects.equals(productionDate, that.productionDate) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, serialNumber, bikeType, pricePerHour, available, productionDate, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BikeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSerialNumber().map(f -> "serialNumber=" + f + ", ").orElse("") +
            optionalBikeType().map(f -> "bikeType=" + f + ", ").orElse("") +
            optionalPricePerHour().map(f -> "pricePerHour=" + f + ", ").orElse("") +
            optionalAvailable().map(f -> "available=" + f + ", ").orElse("") +
            optionalProductionDate().map(f -> "productionDate=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
