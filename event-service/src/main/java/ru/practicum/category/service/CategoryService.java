package ru.practicum.category.service;

import ru.practicum.category.model.Category;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addNew(NewCategoryDto dto);

    CategoryDto update(int catId, NewCategoryDto dto);

    void delete(int catId);

    List<CategoryDto> getCategories(int from, int size);

    Category getCategory(int catId);
}
