package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto addNew(NewCategoryDto dto) {
        Category cat = mapper.mapNewCategoryDtoToCategory(dto);
        return mapper.mapCategoryToCategoryDto(repository.saveAndFlush(cat));
    }

    @Override
    public CategoryDto update(int catId, NewCategoryDto dto) {
        Category cat = checkIfCategoryExist(catId);
        cat.setName(dto.getName());
        return mapper.mapCategoryToCategoryDto(repository.saveAndFlush(cat));
    }

    @Override
    public void delete(int catId) {
        checkIfCategoryExist(catId);
        if (eventRepository.findAllByCategoryId(catId).isEmpty()) {
            repository.deleteById(catId);
        } else {
            throw new ConflictException("Category belongs to event");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        return repository.findAll(page).stream()
                .map(mapper::mapCategoryToCategoryDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategory(int catId) {
        return checkIfCategoryExist(catId);
    }

    private Category checkIfCategoryExist(int id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id=" + id + " was not found")
        );
    }
}
