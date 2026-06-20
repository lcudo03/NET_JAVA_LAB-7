package pl.edu.bikerental.service.mapper;

import static pl.edu.bikerental.domain.BikeAsserts.*;
import static pl.edu.bikerental.domain.BikeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BikeMapperTest {

    private BikeMapper bikeMapper;

    @BeforeEach
    void setUp() {
        bikeMapper = new BikeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBikeSample1();
        var actual = bikeMapper.toEntity(bikeMapper.toDto(expected));
        assertBikeAllPropertiesEquals(expected, actual);
    }
}
