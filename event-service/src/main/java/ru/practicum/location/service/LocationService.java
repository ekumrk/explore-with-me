package ru.practicum.location.service;

import ru.practicum.location.model.Location;
import ru.practicum.location.model.dto.LocationDto;

public interface LocationService {
    Location addNewLocation(LocationDto dto);

    void deleteLocation(Long id);
}
