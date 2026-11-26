package dev.sorokin.eventmanager.location;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;

    public LocationService(LocationRepository locationRepository, LocationEntityConverter locationEntityConverter) {
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
    }

    public List<Location> getAllLocations() {
        var allLocationsList = locationRepository.findAll().stream()
                .map(locationEntityConverter::toDomain)
                .toList();
        log.info("Found {} locations", allLocationsList.size());
        return allLocationsList;
    }

    public Location createLocation(Location location) {
        var locationEntity = locationEntityConverter.toEntity(location);
        var savedLocationEntity = locationRepository.save(locationEntity);
        log.info("Location created with id={}", savedLocationEntity.getId());
        return locationEntityConverter.toDomain(savedLocationEntity);
    }

    public Location getLocationById(Long id) {
        var locationEntity = locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No found location by id=%s".formatted(id)));
        log.info("Found location by id={}", id);
        return locationEntityConverter.toDomain(locationEntity);
    }

    public void deleteLocationById(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("No found location by id=%s".formatted(id));
        }
        log.info("Delete location by id={}", id);
        locationRepository.deleteById(id);
    }

    public Location updateLocation(Long id, Location locationToUpdate) {
        var locationEntity = locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No found location by id=%s".formatted(id)));

        var updatedLocationEntity = locationRepository.save(
                new LocationEntity(
                        locationEntity.getId(),
                        locationToUpdate.name(),
                        locationToUpdate.address(),
                        locationToUpdate.capacity(),
                        locationToUpdate.description()
                )
        );
        log.info("Update location with id={}", id);
        return locationEntityConverter.toDomain(updatedLocationEntity);
    }
}
