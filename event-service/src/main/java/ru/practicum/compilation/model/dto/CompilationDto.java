package ru.practicum.compilation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.model.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompilationDto {
    private Set<EventShortDto> events;

    private long id;

    private boolean pinned;

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}
