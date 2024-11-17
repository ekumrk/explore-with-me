package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.InputEventDto;
import ru.practicum.dto.OutputStatsDto;
import ru.practicum.exception.StatsValidationException;
import ru.practicum.service.StatisticsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public InputEventDto saveEventStats(@RequestBody @Valid InputEventDto dto) {
        return service.saveEventStats(dto);
    }

    @GetMapping("/stats")
    public List<OutputStatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.equals(end) || start.isAfter(end)) {
            throw new StatsValidationException("Некорректные начало и конец!");
        }
        return service.getStats(start, end, uris, unique);
    }
}
