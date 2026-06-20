package pl.edu.bikerental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Customer.
 */
@Table("customer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 50)
    @Column("first_name")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 50)
    @Column("last_name")
    private String lastName;

    @NotNull
    @Column("email")
    private String email;

    @Size(max = 20)
    @Column("phone_number")
    private String phoneNumber;

    @NotNull
    @Column("registration_date")
    private LocalDate registrationDate;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer", "bike" }, allowSetters = true)
    private Set<Rental> rentalses = new HashSet<>();

    @org.springframework.data.annotation.Transient
    private CustomerProfile profile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Customer firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Customer lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Customer email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Customer phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getRegistrationDate() {
        return this.registrationDate;
    }

    public Customer registrationDate(LocalDate registrationDate) {
        this.setRegistrationDate(registrationDate);
        return this;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<Rental> getRentalses() {
        return this.rentalses;
    }

    public void setRentalses(Set<Rental> rentals) {
        if (this.rentalses != null) {
            this.rentalses.forEach(i -> i.setCustomer(null));
        }
        if (rentals != null) {
            rentals.forEach(i -> i.setCustomer(this));
        }
        this.rentalses = rentals;
    }

    public Customer rentalses(Set<Rental> rentals) {
        this.setRentalses(rentals);
        return this;
    }

    public Customer addRentals(Rental rental) {
        this.rentalses.add(rental);
        rental.setCustomer(this);
        return this;
    }

    public Customer removeRentals(Rental rental) {
        this.rentalses.remove(rental);
        rental.setCustomer(null);
        return this;
    }

    public CustomerProfile getProfile() {
        return this.profile;
    }

    public void setProfile(CustomerProfile customerProfile) {
        if (this.profile != null) {
            this.profile.setCustomer(null);
        }
        if (customerProfile != null) {
            customerProfile.setCustomer(this);
        }
        this.profile = customerProfile;
    }

    public Customer profile(CustomerProfile customerProfile) {
        this.setProfile(customerProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", registrationDate='" + getRegistrationDate() + "'" +
            "}";
    }
}
