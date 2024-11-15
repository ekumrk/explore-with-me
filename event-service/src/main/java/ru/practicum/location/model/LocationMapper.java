package ru.practicum.location.model;

import org.mapstruct.Mapper;
import ru.practicum.location.model.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location mapLocationDtoToLocation(LocationDto dto);

    LocationDto mapLocationToLocationDto(Location location);
}
