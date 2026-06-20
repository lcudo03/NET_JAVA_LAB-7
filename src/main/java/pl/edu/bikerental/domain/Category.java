package pl.edu.bikerental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Category.
 */
@Table("category")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column("name")
    private String name;

    @Size(max = 255)
    @Column("description")
    private String description;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "rentalses", "categorieses" }, allowSetters = true)
    private Set<Bike> bikeses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Category name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Bike> getBikeses() {
        return this.bikeses;
    }

    public void setBikeses(Set<Bike> bikes) {
        if (this.bikeses != null) {
            this.bikeses.forEach(i -> i.removeCategories(this));
        }
        if (bikes != null) {
            bikes.forEach(i -> i.addCategories(this));
        }
        this.bikeses = bikes;
    }

    public Category bikeses(Set<Bike> bikes) {
        this.setBikeses(bikes);
        return this;
    }

    public Category addBikes(Bike bike) {
        this.bikeses.add(bike);
        bike.getCategorieses().add(this);
        return this;
    }

    public Category removeBikes(Bike bike) {
        this.bikeses.remove(bike);
        bike.getCategorieses().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return getId() != null && getId().equals(((Category) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
