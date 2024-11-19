package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.comment.model.dto.CommentInfoDto;
import ru.practicum.event.model.EventState;
import ru.practicum.location.model.dto.LocationDto;
import ru.practicum.user.model.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventFullDto {
    private String annotation;

    private CategoryDto category;

    private long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private long id;

    private UserShortDto initiator;

    private LocationDto location;

    private boolean paid;

    private int participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    private boolean requestModeration;

    private EventState state;

    private String title;

    private long views;

    private List<CommentInfoDto> comments = new ArrayList<>();
}
