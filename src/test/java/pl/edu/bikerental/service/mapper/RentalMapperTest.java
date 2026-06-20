package pl.edu.bikerental.service.mapper;

import static pl.edu.bikerental.domain.RentalAsserts.*;
import static pl.edu.bikerental.domain.RentalTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RentalMapperTest {

    private RentalMapper rentalMapper;

    @BeforeEach
    void setUp() {
        rentalMapper = new RentalMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRentalSample1();
        var actual = rentalMapper.toEntity(rentalMapper.toDto(expected));
        assertRentalAllPropertiesEquals(expected, actual);
    }
}
