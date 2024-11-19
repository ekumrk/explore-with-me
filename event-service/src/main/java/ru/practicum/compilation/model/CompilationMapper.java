package ru.practicum.compilation.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.event.model.EventMapper;

@Mapper(uses = EventMapper.class, componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "events", source = "dto.events", ignore = true)
    Compilation mapNewCompilationDtoToCompilation(NewCompilationDto dto);

    CompilationDto mapCompilationToCompilationDto(Compilation compilation);
}
