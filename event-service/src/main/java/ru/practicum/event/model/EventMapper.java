package ru.practicum.event.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "category.id", source = "dto.category")
    Event mapNewEventDtoToEvent(NewEventDto dto);

    EventShortDto mapEventToEventShortDto(Event event);

    EventFullDto mapEventToEventFullDto(Event event);

    @Mapping(target = "category", source = "dto.category", ignore = true)
    @Mapping(target = "location", source = "dto.location", ignore = true)
    Event mapUpdateEventAdminRequestToEvent(UpdateEventAdminRequest dto);

    @Mapping(target = "category", source = "dto.category", ignore = true)
    @Mapping(target = "location", source = "dto.location", ignore = true)
    Event mapUpdateEventUserRequestToEvent(UpdateEventUserRequest dto);
}
