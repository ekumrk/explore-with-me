package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;
    private final CompilationMapper mapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addNewCompilationAdmin(NewCompilationDto dto) {
        Compilation comp = mapper.mapNewCompilationDtoToCompilation(dto);
        comp.setEvents(getEvents(dto.getEvents()));
        return mapper.mapCompilationToCompilationDto(repository.saveAndFlush(comp));
    }

    @Override
    public CompilationDto updateCompilationAdmin(long compId, UpdateCompilationRequest dto) {
        Compilation comp = checkIfCompilationExist(compId);
        comp.setEvents(dto.getEvents() == null ? comp.getEvents() :
                new HashSet<>(eventRepository.findAllById(dto.getEvents())));
        comp.setPinned(dto.getPinned() == null ? comp.getPinned() : dto.getPinned());
        comp.setTitle(dto.getTitle() == null ? comp.getTitle() : dto.getTitle());
        return mapper.mapCompilationToCompilationDto(repository.saveAndFlush(comp));
    }

    @Override
    public void deleteCompilationAdmin(long compId) {
        checkIfCompilationExist(compId);
        repository.deleteById(compId);
        repository.flush();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilationsPublic(boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        return repository.getCompilationsPublic(pinned, page).stream()
                .map(mapper::mapCompilationToCompilationDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationPublic(long compId) {
        return mapper.mapCompilationToCompilationDto(checkIfCompilationExist(compId));
    }

    private Compilation checkIfCompilationExist(long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Compilation with id=" + id + " was not found")
        );
    }

    private Set<Event> getEvents(List<Long> list) {
        if (list == null) {
            return new HashSet<>();
        }

        return list.stream()
                .map(id -> eventRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Event with id=" + id + " was not found")
                ))
                .collect(Collectors.toUnmodifiableSet());
    }
}
