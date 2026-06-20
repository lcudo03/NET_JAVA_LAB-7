package pl.edu.bikerental.service.mapper;

import org.mapstruct.*;
import pl.edu.bikerental.domain.Customer;
import pl.edu.bikerental.service.dto.CustomerDTO;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {}
