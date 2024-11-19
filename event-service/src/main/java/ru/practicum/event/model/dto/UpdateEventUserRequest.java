package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.location.model.dto.LocationDto;
import ru.practicum.request.model.StateAction;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.ANNOTATION_MAX;
import static ru.practicum.constants.Constants.ANNOTATION_MIN;
import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;
import static ru.practicum.constants.Constants.DESCRIPTION_MAX;
import static ru.practicum.constants.Constants.DESCRIPTION_MIN;
import static ru.practicum.constants.Constants.TITLE_MAX;
import static ru.practicum.constants.Constants.TITLE_MIN;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateEventUserRequest {

    @Size(min = ANNOTATION_MIN, max = ANNOTATION_MAX)
    private String annotation;

    private int category;

    @Size(min = DESCRIPTION_MIN, max = DESCRIPTION_MAX)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private int participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = TITLE_MIN, max = TITLE_MAX)
    private String title;
}
