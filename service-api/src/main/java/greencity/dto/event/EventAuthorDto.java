package greencity.dto.event;

public record EventAuthorDto(
        Long id,
        String name,
        Double organizerRating
) {
}
