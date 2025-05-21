package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.service.NewsSubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/newsSubscriber")
@RequiredArgsConstructor
public class NewsSubscriberController {
    private final NewsSubscriberService newsSubscriberService;

    @PostMapping
    @Operation(
        summary = "Subscribe to the news",
        description = "This method allows you to subscribe to the news by providing an email address.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = HttpStatuses.OK,
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = NewsSubscriberRequestDto.class))
                }),
            @ApiResponse(
                responseCode = "400",
                description = HttpStatuses.BAD_REQUEST,
                content = {
                    @Content(schema = @Schema(hidden = true))
                }),
        })
    public ResponseEntity<NewsSubscriberRequestDto> post(
        @Valid @RequestBody @Parameter(
            description = "Email of the subscriber",
            required = true) NewsSubscriberRequestDto request) {
        return ResponseEntity.ok(newsSubscriberService.subscribe(request));
    }
}
