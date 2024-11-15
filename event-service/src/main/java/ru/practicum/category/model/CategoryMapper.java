package ru.practicum.category.model;

import org.mapstruct.Mapper;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category mapNewCategoryDtoToCategory(NewCategoryDto dto);

    CategoryDto mapCategoryToCategoryDto(Category category);
}
