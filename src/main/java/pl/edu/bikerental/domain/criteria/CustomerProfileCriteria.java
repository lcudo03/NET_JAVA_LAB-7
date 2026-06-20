package pl.edu.bikerental.domain.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link pl.edu.bikerental.domain.CustomerProfile} entity. This class is used
 * in {@link pl.edu.bikerental.web.rest.CustomerProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customer-profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerProfileCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter address;

    private StringFilter city;

    private IntegerFilter loyaltyPoints;

    private BooleanFilter verified;

    private LongFilter customerId;

    private Boolean distinct;

    public CustomerProfileCriteria() {}

    public CustomerProfileCriteria(CustomerProfileCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.loyaltyPoints = other.optionalLoyaltyPoints().map(IntegerFilter::copy).orElse(null);
        this.verified = other.optionalVerified().map(BooleanFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerProfileCriteria copy() {
        return new CustomerProfileCriteria(this);
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

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getCity() {
        return city;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter city() {
        if (city == null) {
            setCity(new StringFilter());
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public IntegerFilter getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Optional<IntegerFilter> optionalLoyaltyPoints() {
        return Optional.ofNullable(loyaltyPoints);
    }

    public IntegerFilter loyaltyPoints() {
        if (loyaltyPoints == null) {
            setLoyaltyPoints(new IntegerFilter());
        }
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(IntegerFilter loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public BooleanFilter getVerified() {
        return verified;
    }

    public Optional<BooleanFilter> optionalVerified() {
        return Optional.ofNullable(verified);
    }

    public BooleanFilter verified() {
        if (verified == null) {
            setVerified(new BooleanFilter());
        }
        return verified;
    }

    public void setVerified(BooleanFilter verified) {
        this.verified = verified;
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
        final CustomerProfileCriteria that = (CustomerProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(address, that.address) &&
            Objects.equals(city, that.city) &&
            Objects.equals(loyaltyPoints, that.loyaltyPoints) &&
            Objects.equals(verified, that.verified) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, city, loyaltyPoints, verified, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerProfileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalLoyaltyPoints().map(f -> "loyaltyPoints=" + f + ", ").orElse("") +
            optionalVerified().map(f -> "verified=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
