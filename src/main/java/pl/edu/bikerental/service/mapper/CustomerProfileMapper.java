package pl.edu.bikerental.service.mapper;

import org.mapstruct.*;
import pl.edu.bikerental.domain.Customer;
import pl.edu.bikerental.domain.CustomerProfile;
import pl.edu.bikerental.service.dto.CustomerDTO;
import pl.edu.bikerental.service.dto.CustomerProfileDTO;

/**
 * Mapper for the entity {@link CustomerProfile} and its DTO {@link CustomerProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerProfileMapper extends EntityMapper<CustomerProfileDTO, CustomerProfile> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    CustomerProfileDTO toDto(CustomerProfile s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
