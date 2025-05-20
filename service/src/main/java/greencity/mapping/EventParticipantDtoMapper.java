package greencity.mapping;

import greencity.dto.event.EventParticipantDto;
import greencity.entity.event.Event;
import greencity.enums.EventRole;
import java.util.stream.Collectors;

/**
 * Mapper class for converting {@link Event} and {@link EventRole} entities to {@link EventParticipantDto}.
 * Used to transform event data and participation role into a DTO suitable for client responses.
 *
 * @author Dmytro Kravchuk
 */
public class EventParticipantDtoMapper {
    /**
     * Converts an {@link Event} and a corresponding {@link EventRole} into an {@link EventParticipantDto}.
     * Extracts event details including title, description, author name, tag names, and main image URL.
     *
     * @param event the event entity to map
     * @param role the user's role in the event
     * @return an {@link EventParticipantDto} containing event information and user's role in the event
     * @author Dmytro Kravchuk
     */
    public static EventParticipantDto toDto(Event event, EventRole role) {
        return EventParticipantDto.builder()
            .id(event.getId())
            .title(event.getTitle())
            .description(event.getDescription())
            .isOpen(event.isOpen())
            .authorName(event.getAuthor().getName())
            .tagNames(event.getTags().stream()
                .map(tag -> tag.getTagTranslations().getFirst().getName())
                .collect(Collectors.toList()))
            .mainImageUrl(event.getMainImage().getLink())
            .role(role)
            .build();
    }
}
