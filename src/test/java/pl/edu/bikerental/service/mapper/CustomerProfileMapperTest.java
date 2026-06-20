package pl.edu.bikerental.service.mapper;

import static pl.edu.bikerental.domain.CustomerProfileAsserts.*;
import static pl.edu.bikerental.domain.CustomerProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerProfileMapperTest {

    private CustomerProfileMapper customerProfileMapper;

    @BeforeEach
    void setUp() {
        customerProfileMapper = new CustomerProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCustomerProfileSample1();
        var actual = customerProfileMapper.toEntity(customerProfileMapper.toDto(expected));
        assertCustomerProfileAllPropertiesEquals(expected, actual);
    }
}
