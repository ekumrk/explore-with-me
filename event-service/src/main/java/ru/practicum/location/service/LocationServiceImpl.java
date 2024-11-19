package ru.practicum.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.location.model.Location;
import ru.practicum.location.model.LocationMapper;
import ru.practicum.location.model.dto.LocationDto;
import ru.practicum.location.repository.LocationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository repository;
    private final LocationMapper mapper;

    @Override
    public Location addNewLocation(LocationDto dto) {
        return repository.saveAndFlush(mapper.mapLocationDtoToLocation(dto));
    }

    @Override
    public void deleteLocation(Long id) {
        repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Location not found"));
        repository.deleteById(id);
        repository.flush();
    }
}
