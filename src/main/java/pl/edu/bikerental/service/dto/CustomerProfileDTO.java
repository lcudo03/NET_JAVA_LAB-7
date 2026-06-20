package pl.edu.bikerental.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pl.edu.bikerental.domain.CustomerProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerProfileDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 150)
    private String address;

    @NotNull
    @Size(max = 80)
    private String city;

    @Min(value = 0)
    private Integer loyaltyPoints;

    @NotNull
    private Boolean verified;

    @NotNull
    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerProfileDTO)) {
            return false;
        }

        CustomerProfileDTO customerProfileDTO = (CustomerProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerProfileDTO{" +
            "id=" + getId() +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            ", verified='" + getVerified() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
