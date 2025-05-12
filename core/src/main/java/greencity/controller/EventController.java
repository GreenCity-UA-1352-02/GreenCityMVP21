package greencity.controller;

import greencity.annotations.EventImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.event.AddEventRequest;
import greencity.dto.event.EventResponse;
import greencity.dto.event.UpdateEventRequest;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Method for creating a new {@link EventResponse}.
     *
     * @param addEventRequest - DTO for creating a new event.
     * @param images          - list of event images.
     * @param principal       - authenticated user.
     * @return created {@link EventResponse} instance.
     * @author [Olexandr Pohranychnyi]
     */

    @Operation(summary = "Create a new event")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EventResponse> create(
        @RequestPart @Validated AddEventRequest addEventRequest,
        @RequestPart @EventImageValidation List<MultipartFile> images,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(eventService.save(addEventRequest, images, principal.getName()));
    }

    /**
     * Method for updating an existing {@link EventResponse}.
     * Accessible for users with role ADMIN or the author of the event.
     *
     * @param updateEventRequest - DTO for updating the event.
     * @param images             - optional list of updated event images.
     * @return updated {@link EventResponse} instance.
     * @author [Olexandr Pohranychnyi]
     */
    @Operation(summary = "Update event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PreAuthorize
        ("hasRole('ADMIN') || @eventRepo.existsByIdAndAuthor_Email(#updateEventRequest.id, principal.username)")
    @PutMapping(value = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EventResponse> update(
        @RequestPart @Validated UpdateEventRequest updateEventRequest,
        @RequestPart(required = false) @EventImageValidation List<MultipartFile> images
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(eventService.update(updateEventRequest, images));
    }

    /**
     * Method for deleting an {@link EventResponse} by ID.
     * Accessible for users with role ADMIN or the author of the event.
     *
     * @param id - ID of the event to delete.
     * @return HTTP 200 OK if the event was deleted successfully.
     * @author [Olexandr Pohranychnyi]
     */
    @Operation(summary = "Delete event")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN') || @eventRepo.existsByIdAndAuthor_Email(#id, principal.username)")
    @DeleteMapping(value = "/delete/{eventId}")
    public ResponseEntity<Object> deleteEvent(@PathVariable("eventId") Long id) {
        eventService.delete(id);
        return ResponseEntity.ok().build();
    }
}
