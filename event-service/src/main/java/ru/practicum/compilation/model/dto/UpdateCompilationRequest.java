package ru.practicum.compilation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateCompilationRequest {
    private List<Long> events;

    private Boolean pinned;

    @Length(min = 1, max = 50)
    private String title;
}
