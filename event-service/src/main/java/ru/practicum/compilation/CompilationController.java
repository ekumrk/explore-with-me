package ru.practicum.compilation;

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
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.Constants.FROM;
import static ru.practicum.constants.Constants.SIZE;

@RestController
@RequiredArgsConstructor
@Validated
public class CompilationController {

    private final CompilationService service;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/admin/compilations", method = RequestMethod.POST)
    public CompilationDto addNewCompilationAdmin(@RequestBody @Valid NewCompilationDto dto) {
        return service.addNewCompilationAdmin(dto);
    }

    @RequestMapping(value = "/admin/compilations/{compId}", method = RequestMethod.PATCH)
    public CompilationDto updateCompilationAdmin(@PathVariable @Positive Long compId,
                                                 @RequestBody @Valid UpdateCompilationRequest dto) {
        return service.updateCompilationAdmin(compId, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/admin/compilations/{compId}", method = RequestMethod.DELETE)
    public void deleteCompilationAdmin(@PathVariable Long compId) {
        service.deleteCompilationAdmin(compId);
    }

    @RequestMapping(value = "/compilations", method = RequestMethod.GET)
    public List<CompilationDto> getCompilationsPublic(@RequestParam(required = false) boolean pinned,
                                                      @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        return service.getCompilationsPublic(pinned, from, size);
    }

    @RequestMapping(value = "/compilations/{compId}", method = RequestMethod.GET)
    public CompilationDto getCompilationPublic(@PathVariable @Positive Long compId) {
        return service.getCompilationPublic(compId);
    }
}
