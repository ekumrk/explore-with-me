package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.Constants.FROM;
import static ru.practicum.constants.Constants.SIZE;

@RestController
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService service;
    private final CategoryMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/admin/categories", method = RequestMethod.POST)
    public CategoryDto addNewCategory(@RequestBody @Valid NewCategoryDto dto) {
        return service.addNew(dto);
    }

    @RequestMapping(value = "/admin/categories/{catId}", method = RequestMethod.PATCH)
    public CategoryDto updateCategory(@PathVariable @Positive Integer catId,
                                      @RequestBody @Valid NewCategoryDto dto) {
        return service.update(catId, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/admin/categories/{catId}", method = RequestMethod.DELETE)
    public void deleteCategory(@PathVariable @Positive Integer catId) {
        service.delete(catId);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        return service.getCategories(from, size);
    }

    @RequestMapping(value = "/categories/{catId}", method = RequestMethod.GET)
    public CategoryDto getCategory(@PathVariable @Positive Integer catId) {
        return mapper.mapCategoryToCategoryDto(service.getCategory(catId));
    }
}
