package pl.edu.bikerental.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import pl.edu.bikerental.domain.enumeration.RentalStatus;

/**
 * A DTO for the {@link pl.edu.bikerental.domain.Rental} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RentalDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @DecimalMin(value = "0")
    private Float totalPrice;

    @NotNull
    private RentalStatus status;

    @NotNull
    private CustomerDTO customer;

    @NotNull
    private BikeDTO bike;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public BikeDTO getBike() {
        return bike;
    }

    public void setBike(BikeDTO bike) {
        this.bike = bike;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RentalDTO)) {
            return false;
        }

        RentalDTO rentalDTO = (RentalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rentalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RentalDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", totalPrice=" + getTotalPrice() +
            ", status='" + getStatus() + "'" +
            ", customer=" + getCustomer() +
            ", bike=" + getBike() +
            "}";
    }
}
