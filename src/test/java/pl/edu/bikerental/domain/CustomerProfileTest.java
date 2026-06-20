package pl.edu.bikerental.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.bikerental.domain.CustomerProfileTestSamples.*;
import static pl.edu.bikerental.domain.CustomerTestSamples.*;

import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class CustomerProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerProfile.class);
        CustomerProfile customerProfile1 = getCustomerProfileSample1();
        CustomerProfile customerProfile2 = new CustomerProfile();
        assertThat(customerProfile1).isNotEqualTo(customerProfile2);

        customerProfile2.setId(customerProfile1.getId());
        assertThat(customerProfile1).isEqualTo(customerProfile2);

        customerProfile2 = getCustomerProfileSample2();
        assertThat(customerProfile1).isNotEqualTo(customerProfile2);
    }

    @Test
    void customerTest() {
        CustomerProfile customerProfile = getCustomerProfileRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        customerProfile.setCustomer(customerBack);
        assertThat(customerProfile.getCustomer()).isEqualTo(customerBack);

        customerProfile.customer(null);
        assertThat(customerProfile.getCustomer()).isNull();
    }
}
