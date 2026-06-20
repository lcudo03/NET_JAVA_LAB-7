package pl.edu.bikerental.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.Category;
import pl.edu.bikerental.service.dto.BikeDTO;
import pl.edu.bikerental.service.dto.CategoryDTO;

/**
 * Mapper for the entity {@link Bike} and its DTO {@link BikeDTO}.
 */
@Mapper(componentModel = "spring")
public interface BikeMapper extends EntityMapper<BikeDTO, Bike> {
    @Mapping(target = "categorieses", source = "categorieses", qualifiedByName = "categoryIdSet")
    BikeDTO toDto(Bike s);

    @Mapping(target = "removeCategories", ignore = true)
    Bike toEntity(BikeDTO bikeDTO);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);

    @Named("categoryIdSet")
    default Set<CategoryDTO> toDtoCategoryIdSet(Set<Category> category) {
        return category.stream().map(this::toDtoCategoryId).collect(Collectors.toSet());
    }
}
