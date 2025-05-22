package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ValidSource;
import greencity.constant.HttpStatuses;
import greencity.dto.notification.NotificationDtoRequest;
import greencity.dto.notification.NotificationEvent;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;

import jakarta.validation.Valid;
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
    public ResponseEntity<NotificationEvent> post(@Valid @RequestBody NotificationEvent notificationEvent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.saveNotification(notificationEvent));
    }

    @Operation(summary = "Get all notifications.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "410", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/")
    public ResponseEntity<List<NotificationEvent>> get() {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.findAllNotifications());
    }

    @Operation(summary = "Get all notifications for specific user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/user")
    public ResponseEntity<List<NotificationDtoRequest>> getNotificationsForUser(
        @Parameter(hidden = true) @CurrentUser UserVO userVO,
        @RequestParam(defaultValue = "ALL") @ValidSource String filter) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(notificationService.findUserNotifications(userVO.getId(), filter));
    }

    @Operation(summary = "Delete notification by id.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @DeleteMapping
    public ResponseEntity<Object> delete(@Parameter(hidden = true) @CurrentUser UserVO userVO,
        @RequestParam Long id) {
        notificationService.deleteNotification(id, userVO.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
