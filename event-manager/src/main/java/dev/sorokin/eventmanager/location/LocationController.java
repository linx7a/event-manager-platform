package dev.sorokin.eventmanager.location;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);
    private final LocationService locationService;
    private final LocationDtoConverter locationDtoConverter;

    public LocationController(LocationService locationService, LocationDtoConverter locationDtoConverter) {
        this.locationService = locationService;
        this.locationDtoConverter = locationDtoConverter;
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> findAllLocations() {
        log.info("Get request to find all locations");
        return ResponseEntity.status(HttpStatus.OK)
                .body(locationService.getAllLocations().stream()
                        .map(locationDtoConverter::toDto)
                        .toList());
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @Valid @RequestBody LocationDto locationToCreate) {
        log.info("Post request to create location");
        var createdLocation = locationService.createLocation(locationDtoConverter.toDomain(locationToCreate));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationDtoConverter.toDto(createdLocation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> findLocation(
            @PathVariable Long id
    ) {
        log.info("Get request to get location by id={}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(locationDtoConverter.toDto(locationService.getLocationById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Long id
    ) {
        log.info("Delete request to delete location by id={}", id);
        locationService.deleteLocationById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationDto locationToUpdate
    ) {
        log.info("Put request to update location by id={}", id);
        var updatedLocation = locationService.updateLocation(id, locationDtoConverter.toDomain(locationToUpdate));
        return ResponseEntity.status(HttpStatus.OK)
                .body(locationDtoConverter.toDto(updatedLocation));
    }
}
