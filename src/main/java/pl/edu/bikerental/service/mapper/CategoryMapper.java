package pl.edu.bikerental.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.Category;
import pl.edu.bikerental.service.dto.BikeDTO;
import pl.edu.bikerental.service.dto.CategoryDTO;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @Mapping(target = "bikeses", source = "bikeses", qualifiedByName = "bikeIdSet")
    CategoryDTO toDto(Category s);

    @Mapping(target = "bikeses", ignore = true)
    @Mapping(target = "removeBikes", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    @Named("bikeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BikeDTO toDtoBikeId(Bike bike);

    @Named("bikeIdSet")
    default Set<BikeDTO> toDtoBikeIdSet(Set<Bike> bike) {
        return bike.stream().map(this::toDtoBikeId).collect(Collectors.toSet());
    }
}
