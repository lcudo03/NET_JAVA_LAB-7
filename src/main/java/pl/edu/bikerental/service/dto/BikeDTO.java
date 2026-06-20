package pl.edu.bikerental.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import pl.edu.bikerental.domain.enumeration.BikeType;

/**
 * A DTO for the {@link pl.edu.bikerental.domain.Bike} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BikeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String name;

    @NotNull
    private String serialNumber;

    @NotNull
    private BikeType bikeType;

    @NotNull
    @DecimalMin(value = "0")
    private Float pricePerHour;

    @NotNull
    private Boolean available;

    private LocalDate productionDate;

    private Set<CategoryDTO> categorieses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BikeType getBikeType() {
        return bikeType;
    }

    public void setBikeType(BikeType bikeType) {
        this.bikeType = bikeType;
    }

    public Float getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public Set<CategoryDTO> getCategorieses() {
        return categorieses;
    }

    public void setCategorieses(Set<CategoryDTO> categorieses) {
        this.categorieses = categorieses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BikeDTO)) {
            return false;
        }

        BikeDTO bikeDTO = (BikeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bikeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BikeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", serialNumber='" + getSerialNumber() + "'" +
            ", bikeType='" + getBikeType() + "'" +
            ", pricePerHour=" + getPricePerHour() +
            ", available='" + getAvailable() + "'" +
            ", productionDate='" + getProductionDate() + "'" +
            ", categorieses=" + getCategorieses() +
            "}";
    }
}
