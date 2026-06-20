package pl.edu.bikerental.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class RentalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RentalDTO.class);
        RentalDTO rentalDTO1 = new RentalDTO();
        rentalDTO1.setId(1L);
        RentalDTO rentalDTO2 = new RentalDTO();
        assertThat(rentalDTO1).isNotEqualTo(rentalDTO2);
        rentalDTO2.setId(rentalDTO1.getId());
        assertThat(rentalDTO1).isEqualTo(rentalDTO2);
        rentalDTO2.setId(2L);
        assertThat(rentalDTO1).isNotEqualTo(rentalDTO2);
        rentalDTO1.setId(null);
        assertThat(rentalDTO1).isNotEqualTo(rentalDTO2);
    }
}
