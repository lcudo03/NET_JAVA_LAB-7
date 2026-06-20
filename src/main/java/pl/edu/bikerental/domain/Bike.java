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
import pl.edu.bikerental.domain.enumeration.BikeType;

/**
 * A Bike.
 */
@Table("bike")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bike implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column("name")
    private String name;

    @NotNull
    @Column("serial_number")
    private String serialNumber;

    @NotNull
    @Column("bike_type")
    private BikeType bikeType;

    @NotNull
    @DecimalMin(value = "0")
    @Column("price_per_hour")
    private Float pricePerHour;

    @NotNull
    @Column("available")
    private Boolean available;

    @Column("production_date")
    private LocalDate productionDate;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer", "bike" }, allowSetters = true)
    private Set<Rental> rentalses = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "bikeses" }, allowSetters = true)
    private Set<Category> categorieses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bike id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Bike name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public Bike serialNumber(String serialNumber) {
        this.setSerialNumber(serialNumber);
        return this;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BikeType getBikeType() {
        return this.bikeType;
    }

    public Bike bikeType(BikeType bikeType) {
        this.setBikeType(bikeType);
        return this;
    }

    public void setBikeType(BikeType bikeType) {
        this.bikeType = bikeType;
    }

    public Float getPricePerHour() {
        return this.pricePerHour;
    }

    public Bike pricePerHour(Float pricePerHour) {
        this.setPricePerHour(pricePerHour);
        return this;
    }

    public void setPricePerHour(Float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Boolean getAvailable() {
        return this.available;
    }

    public Bike available(Boolean available) {
        this.setAvailable(available);
        return this;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalDate getProductionDate() {
        return this.productionDate;
    }

    public Bike productionDate(LocalDate productionDate) {
        this.setProductionDate(productionDate);
        return this;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public Set<Rental> getRentalses() {
        return this.rentalses;
    }

    public void setRentalses(Set<Rental> rentals) {
        if (this.rentalses != null) {
            this.rentalses.forEach(i -> i.setBike(null));
        }
        if (rentals != null) {
            rentals.forEach(i -> i.setBike(this));
        }
        this.rentalses = rentals;
    }

    public Bike rentalses(Set<Rental> rentals) {
        this.setRentalses(rentals);
        return this;
    }

    public Bike addRentals(Rental rental) {
        this.rentalses.add(rental);
        rental.setBike(this);
        return this;
    }

    public Bike removeRentals(Rental rental) {
        this.rentalses.remove(rental);
        rental.setBike(null);
        return this;
    }

    public Set<Category> getCategorieses() {
        return this.categorieses;
    }

    public void setCategorieses(Set<Category> categories) {
        this.categorieses = categories;
    }

    public Bike categorieses(Set<Category> categories) {
        this.setCategorieses(categories);
        return this;
    }

    public Bike addCategories(Category category) {
        this.categorieses.add(category);
        return this;
    }

    public Bike removeCategories(Category category) {
        this.categorieses.remove(category);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bike)) {
            return false;
        }
        return getId() != null && getId().equals(((Bike) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bike{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", serialNumber='" + getSerialNumber() + "'" +
            ", bikeType='" + getBikeType() + "'" +
            ", pricePerHour=" + getPricePerHour() +
            ", available='" + getAvailable() + "'" +
            ", productionDate='" + getProductionDate() + "'" +
            "}";
    }
}
