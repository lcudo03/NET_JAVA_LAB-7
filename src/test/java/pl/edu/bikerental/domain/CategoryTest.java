package pl.edu.bikerental.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.bikerental.domain.BikeTestSamples.*;
import static pl.edu.bikerental.domain.CategoryTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void bikesTest() {
        Category category = getCategoryRandomSampleGenerator();
        Bike bikeBack = getBikeRandomSampleGenerator();

        category.addBikes(bikeBack);
        assertThat(category.getBikeses()).containsOnly(bikeBack);
        assertThat(bikeBack.getCategorieses()).containsOnly(category);

        category.removeBikes(bikeBack);
        assertThat(category.getBikeses()).doesNotContain(bikeBack);
        assertThat(bikeBack.getCategorieses()).doesNotContain(category);

        category.bikeses(new HashSet<>(Set.of(bikeBack)));
        assertThat(category.getBikeses()).containsOnly(bikeBack);
        assertThat(bikeBack.getCategorieses()).containsOnly(category);

        category.setBikeses(new HashSet<>());
        assertThat(category.getBikeses()).doesNotContain(bikeBack);
        assertThat(bikeBack.getCategorieses()).doesNotContain(category);
    }
}
