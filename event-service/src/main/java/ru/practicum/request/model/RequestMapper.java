package ru.practicum.request.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.model.dto.ParticipationRequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "event", source = "req.event.id")
    @Mapping(target = "requester", source = "req.requester.id")
    ParticipationRequestDto mapRequestToParticipationRequestDto(Request req);
}
