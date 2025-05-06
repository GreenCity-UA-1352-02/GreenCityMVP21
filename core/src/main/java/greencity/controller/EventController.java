package greencity.controller;

import greencity.annotations.EventImageValidation;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.service.EventService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EventDto> create(
        @RequestPart @Validated AddEventDtoRequest addEventDtoRequest,
        @RequestPart(required = false) @EventImageValidation List<MultipartFile> images,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(eventService.save(addEventDtoRequest, images, principal.getName()));
    }

    @PreAuthorize("hasRole('ADMIN') || @eventRepo.existsByIdAndAuthor_Email(#eventDto.id, principal.username)")
    @PutMapping(value = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EventDto> update(
        @RequestPart EventDto eventDto,
        @RequestPart(required = false) @EventImageValidation List<MultipartFile> images
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(eventService.update(eventDto, images));
    }

    @PreAuthorize("hasRole('ADMIN') || @eventRepo.existsByIdAndAuthor_Email(#id, principal.username)")
    @DeleteMapping(value = "/delete/{eventId}")
    public ResponseEntity<Object> deleteEvent(@PathVariable("eventId") Long id) {
        eventService.delete(id);
        return ResponseEntity.ok().build();
    }
}
