package ru.practicum.compilation.service;

import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addNewCompilationAdmin(NewCompilationDto dto);

    CompilationDto updateCompilationAdmin(long compId, UpdateCompilationRequest dto);

    void deleteCompilationAdmin(long compId);

    List<CompilationDto> getCompilationsPublic(boolean pinned, int from, int size);

    CompilationDto getCompilationPublic(long compId);
}
