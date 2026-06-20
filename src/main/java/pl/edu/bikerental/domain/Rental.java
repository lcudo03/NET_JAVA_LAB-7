package pl.edu.bikerental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pl.edu.bikerental.domain.enumeration.RentalStatus;

/**
 * A Rental.
 */
@Table("rental")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Rental implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @DecimalMin(value = "0")
    @Column("total_price")
    private Float totalPrice;

    @NotNull
    @Column("status")
    private RentalStatus status;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "rentalses", "profile" }, allowSetters = true)
    private Customer customer;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "rentalses", "categorieses" }, allowSetters = true)
    private Bike bike;

    @Column("customer_id")
    private Long customerId;

    @Column("bike_id")
    private Long bikeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rental id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Rental startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Rental endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Float getTotalPrice() {
        return this.totalPrice;
    }

    public Rental totalPrice(Float totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public RentalStatus getStatus() {
        return this.status;
    }

    public Rental status(RentalStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public Rental customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Bike getBike() {
        return this.bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
        this.bikeId = bike != null ? bike.getId() : null;
    }

    public Rental bike(Bike bike) {
        this.setBike(bike);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    public Long getBikeId() {
        return this.bikeId;
    }

    public void setBikeId(Long bike) {
        this.bikeId = bike;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rental)) {
            return false;
        }
        return getId() != null && getId().equals(((Rental) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rental{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", totalPrice=" + getTotalPrice() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
