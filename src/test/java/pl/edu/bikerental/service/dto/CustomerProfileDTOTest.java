package pl.edu.bikerental.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class CustomerProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerProfileDTO.class);
        CustomerProfileDTO customerProfileDTO1 = new CustomerProfileDTO();
        customerProfileDTO1.setId(1L);
        CustomerProfileDTO customerProfileDTO2 = new CustomerProfileDTO();
        assertThat(customerProfileDTO1).isNotEqualTo(customerProfileDTO2);
        customerProfileDTO2.setId(customerProfileDTO1.getId());
        assertThat(customerProfileDTO1).isEqualTo(customerProfileDTO2);
        customerProfileDTO2.setId(2L);
        assertThat(customerProfileDTO1).isNotEqualTo(customerProfileDTO2);
        customerProfileDTO1.setId(null);
        assertThat(customerProfileDTO1).isNotEqualTo(customerProfileDTO2);
    }
}
