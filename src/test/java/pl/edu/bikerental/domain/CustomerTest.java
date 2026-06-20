package pl.edu.bikerental.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.bikerental.domain.CustomerProfileTestSamples.*;
import static pl.edu.bikerental.domain.CustomerTestSamples.*;
import static pl.edu.bikerental.domain.RentalTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void rentalsTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Rental rentalBack = getRentalRandomSampleGenerator();

        customer.addRentals(rentalBack);
        assertThat(customer.getRentalses()).containsOnly(rentalBack);
        assertThat(rentalBack.getCustomer()).isEqualTo(customer);

        customer.removeRentals(rentalBack);
        assertThat(customer.getRentalses()).doesNotContain(rentalBack);
        assertThat(rentalBack.getCustomer()).isNull();

        customer.rentalses(new HashSet<>(Set.of(rentalBack)));
        assertThat(customer.getRentalses()).containsOnly(rentalBack);
        assertThat(rentalBack.getCustomer()).isEqualTo(customer);

        customer.setRentalses(new HashSet<>());
        assertThat(customer.getRentalses()).doesNotContain(rentalBack);
        assertThat(rentalBack.getCustomer()).isNull();
    }

    @Test
    void profileTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        CustomerProfile customerProfileBack = getCustomerProfileRandomSampleGenerator();

        customer.setProfile(customerProfileBack);
        assertThat(customer.getProfile()).isEqualTo(customerProfileBack);
        assertThat(customerProfileBack.getCustomer()).isEqualTo(customer);

        customer.profile(null);
        assertThat(customer.getProfile()).isNull();
        assertThat(customerProfileBack.getCustomer()).isNull();
    }
}
