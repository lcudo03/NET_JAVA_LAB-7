package pl.edu.bikerental.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pl.edu.bikerental.web.rest.TestUtil;

class BikeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BikeDTO.class);
        BikeDTO bikeDTO1 = new BikeDTO();
        bikeDTO1.setId(1L);
        BikeDTO bikeDTO2 = new BikeDTO();
        assertThat(bikeDTO1).isNotEqualTo(bikeDTO2);
        bikeDTO2.setId(bikeDTO1.getId());
        assertThat(bikeDTO1).isEqualTo(bikeDTO2);
        bikeDTO2.setId(2L);
        assertThat(bikeDTO1).isNotEqualTo(bikeDTO2);
        bikeDTO1.setId(null);
        assertThat(bikeDTO1).isNotEqualTo(bikeDTO2);
    }
}
