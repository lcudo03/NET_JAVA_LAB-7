package pl.edu.bikerental.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CustomerProfile.
 */
@Table("customer_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column("address")
    private String address;

    @NotNull
    @Size(max = 80)
    @Column("city")
    private String city;

    @Min(value = 0)
    @Column("loyalty_points")
    private Integer loyaltyPoints;

    @NotNull
    @Column("verified")
    private Boolean verified;

    @org.springframework.data.annotation.Transient
    private Customer customer;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomerProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public CustomerProfile address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public CustomerProfile city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getLoyaltyPoints() {
        return this.loyaltyPoints;
    }

    public CustomerProfile loyaltyPoints(Integer loyaltyPoints) {
        this.setLoyaltyPoints(loyaltyPoints);
        return this;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public CustomerProfile verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public CustomerProfile customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerProfile{" +
            "id=" + getId() +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            ", verified='" + getVerified() + "'" +
            "}";
    }
}
