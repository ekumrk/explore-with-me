package ru.practicum.comment.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.user.model.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShortCommentDto {
    private String text;

    private EventShortDto event;

    private UserShortDto author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
}
