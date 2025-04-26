package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.notification.NotificationEvent;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Add new notification.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = NotificationEvent.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping("/")
    public ResponseEntity<NotificationEvent> post(@RequestBody NotificationEvent notificationEvent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.saveNotification(notificationEvent));
    }

    @GetMapping("/")
    public ResponseEntity<List<NotificationEvent>> get() {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.findAllNotifications());
    }
}
