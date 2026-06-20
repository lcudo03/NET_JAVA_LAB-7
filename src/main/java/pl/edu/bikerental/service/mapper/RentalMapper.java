package pl.edu.bikerental.service.mapper;

import org.mapstruct.*;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.Customer;
import pl.edu.bikerental.domain.Rental;
import pl.edu.bikerental.service.dto.BikeDTO;
import pl.edu.bikerental.service.dto.CustomerDTO;
import pl.edu.bikerental.service.dto.RentalDTO;

/**
 * Mapper for the entity {@link Rental} and its DTO {@link RentalDTO}.
 */
@Mapper(componentModel = "spring")
public interface RentalMapper extends EntityMapper<RentalDTO, Rental> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    @Mapping(target = "bike", source = "bike", qualifiedByName = "bikeId")
    RentalDTO toDto(Rental s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("bikeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BikeDTO toDtoBikeId(Bike bike);
}
