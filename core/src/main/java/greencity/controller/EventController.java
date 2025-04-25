package greencity.controller;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EventDto> post(
            @RequestPart AddEventDtoRequest addEventDtoRequest,
            @RequestPart(required = false) List<MultipartFile> images,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(addEventDtoRequest, images, principal.getName()));
    }
}
