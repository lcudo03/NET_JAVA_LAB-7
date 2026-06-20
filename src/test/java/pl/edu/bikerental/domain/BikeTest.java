package pl.edu.bikerental.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.bikerental.domain.BikeTestSamples.*;
import static pl.edu.bikerental.domain.CategoryTestSamples.*;
import static pl.edu.bikerental.domain.RentalTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class BikeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bike.class);
        Bike bike1 = getBikeSample1();
        Bike bike2 = new Bike();
        assertThat(bike1).isNotEqualTo(bike2);

        bike2.setId(bike1.getId());
        assertThat(bike1).isEqualTo(bike2);

        bike2 = getBikeSample2();
        assertThat(bike1).isNotEqualTo(bike2);
    }

    @Test
    void rentalsTest() {
        Bike bike = getBikeRandomSampleGenerator();
        Rental rentalBack = getRentalRandomSampleGenerator();

        bike.addRentals(rentalBack);
        assertThat(bike.getRentalses()).containsOnly(rentalBack);
        assertThat(rentalBack.getBike()).isEqualTo(bike);

        bike.removeRentals(rentalBack);
        assertThat(bike.getRentalses()).doesNotContain(rentalBack);
        assertThat(rentalBack.getBike()).isNull();

        bike.rentalses(new HashSet<>(Set.of(rentalBack)));
        assertThat(bike.getRentalses()).containsOnly(rentalBack);
        assertThat(rentalBack.getBike()).isEqualTo(bike);

        bike.setRentalses(new HashSet<>());
        assertThat(bike.getRentalses()).doesNotContain(rentalBack);
        assertThat(rentalBack.getBike()).isNull();
    }

    @Test
    void categoriesTest() {
        Bike bike = getBikeRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        bike.addCategories(categoryBack);
        assertThat(bike.getCategorieses()).containsOnly(categoryBack);

        bike.removeCategories(categoryBack);
        assertThat(bike.getCategorieses()).doesNotContain(categoryBack);

        bike.categorieses(new HashSet<>(Set.of(categoryBack)));
        assertThat(bike.getCategorieses()).containsOnly(categoryBack);

        bike.setCategorieses(new HashSet<>());
        assertThat(bike.getCategorieses()).doesNotContain(categoryBack);
    }
}
