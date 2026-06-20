package pl.edu.bikerental.domain.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import pl.edu.bikerental.domain.enumeration.RentalStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link pl.edu.bikerental.domain.Rental} entity. This class is used
 * in {@link pl.edu.bikerental.web.rest.RentalResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rentals?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RentalCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RentalStatus
     */
    public static class RentalStatusFilter extends Filter<RentalStatus> {

        public RentalStatusFilter() {}

        public RentalStatusFilter(RentalStatusFilter filter) {
            super(filter);
        }

        @Override
        public RentalStatusFilter copy() {
            return new RentalStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private FloatFilter totalPrice;

    private RentalStatusFilter status;

    private LongFilter customerId;

    private LongFilter bikeId;

    private Boolean distinct;

    public RentalCriteria() {}

    public RentalCriteria(RentalCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(LocalDateFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(LocalDateFilter::copy).orElse(null);
        this.totalPrice = other.optionalTotalPrice().map(FloatFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(RentalStatusFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.bikeId = other.optionalBikeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RentalCriteria copy() {
        return new RentalCriteria(this);
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

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public Optional<LocalDateFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public LocalDateFilter startDate() {
        if (startDate == null) {
            setStartDate(new LocalDateFilter());
        }
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public LocalDateFilter getEndDate() {
        return endDate;
    }

    public Optional<LocalDateFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public LocalDateFilter endDate() {
        if (endDate == null) {
            setEndDate(new LocalDateFilter());
        }
        return endDate;
    }

    public void setEndDate(LocalDateFilter endDate) {
        this.endDate = endDate;
    }

    public FloatFilter getTotalPrice() {
        return totalPrice;
    }

    public Optional<FloatFilter> optionalTotalPrice() {
        return Optional.ofNullable(totalPrice);
    }

    public FloatFilter totalPrice() {
        if (totalPrice == null) {
            setTotalPrice(new FloatFilter());
        }
        return totalPrice;
    }

    public void setTotalPrice(FloatFilter totalPrice) {
        this.totalPrice = totalPrice;
    }

    public RentalStatusFilter getStatus() {
        return status;
    }

    public Optional<RentalStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public RentalStatusFilter status() {
        if (status == null) {
            setStatus(new RentalStatusFilter());
        }
        return status;
    }

    public void setStatus(RentalStatusFilter status) {
        this.status = status;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getBikeId() {
        return bikeId;
    }

    public Optional<LongFilter> optionalBikeId() {
        return Optional.ofNullable(bikeId);
    }

    public LongFilter bikeId() {
        if (bikeId == null) {
            setBikeId(new LongFilter());
        }
        return bikeId;
    }

    public void setBikeId(LongFilter bikeId) {
        this.bikeId = bikeId;
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
        final RentalCriteria that = (RentalCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(totalPrice, that.totalPrice) &&
            Objects.equals(status, that.status) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(bikeId, that.bikeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, totalPrice, status, customerId, bikeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RentalCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalTotalPrice().map(f -> "totalPrice=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalBikeId().map(f -> "bikeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
