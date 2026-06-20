package pl.edu.bikerental.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.bikerental.domain.BikeTestSamples.*;
import static pl.edu.bikerental.domain.CustomerTestSamples.*;
import static pl.edu.bikerental.domain.RentalTestSamples.*;

import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class RentalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rental.class);
        Rental rental1 = getRentalSample1();
        Rental rental2 = new Rental();
        assertThat(rental1).isNotEqualTo(rental2);

        rental2.setId(rental1.getId());
        assertThat(rental1).isEqualTo(rental2);

        rental2 = getRentalSample2();
        assertThat(rental1).isNotEqualTo(rental2);
    }

    @Test
    void customerTest() {
        Rental rental = getRentalRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        rental.setCustomer(customerBack);
        assertThat(rental.getCustomer()).isEqualTo(customerBack);

        rental.customer(null);
        assertThat(rental.getCustomer()).isNull();
    }

    @Test
    void bikeTest() {
        Rental rental = getRentalRandomSampleGenerator();
        Bike bikeBack = getBikeRandomSampleGenerator();

        rental.setBike(bikeBack);
        assertThat(rental.getBike()).isEqualTo(bikeBack);

        rental.bike(null);
        assertThat(rental.getBike()).isNull();
    }
}
