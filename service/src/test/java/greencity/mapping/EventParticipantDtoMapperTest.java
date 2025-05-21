package greencity.mapping;

import greencity.dto.event.EventParticipantDto;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventImage;
import greencity.entity.localization.TagTranslation;
import greencity.enums.EventRole;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventParticipantDtoMapperTest {

    @Test
    void givenValidEventAndRole_whenToDto_thenMappedSuccessfully_200() {
        User author = new User();
        author.setName("John Doe");

        TagTranslation translation = new TagTranslation();
        translation.setName("Eco");

        Tag tag = new Tag();
        tag.setTagTranslations(List.of(translation));

        EventImage image = new EventImage();
        image.setLink("http://image.url");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("This is a test event.");
        event.setOpen(true);
        event.setAuthor(author);
        event.setTags(List.of(tag));
        event.setMainImage(image);

        EventParticipantDto dto = EventParticipantDtoMapper.toDto(event, EventRole.ATTENDEE);

        assertEquals(1L, dto.getId());
        assertEquals("Test Event", dto.getTitle());
        assertEquals("This is a test event.", dto.getDescription());
        assertTrue(dto.isOpen());
        assertEquals("John Doe", dto.getAuthorName());
        assertEquals(List.of("Eco"), dto.getTagNames());
        assertEquals("http://image.url", dto.getMainImageUrl());
        assertEquals(EventRole.ATTENDEE, dto.getRole());
    }

    @Test
    void givenEventWithNoTags_whenToDto_thenEmptyTagListReturned_200() {
        User author = new User();
        author.setName("Jane Doe");

        EventImage image = new EventImage();
        image.setLink("http://test.image");

        Event event = new Event();
        event.setId(2L);
        event.setTitle("Another Event");
        event.setDescription("Empty tags test.");
        event.setOpen(false);
        event.setAuthor(author);
        event.setTags(Collections.emptyList());
        event.setMainImage(image);

        EventParticipantDto dto = EventParticipantDtoMapper.toDto(event, EventRole.ORGANIZER);

        assertEquals(2L, dto.getId());
        assertEquals("Another Event", dto.getTitle());
        assertEquals("Empty tags test.", dto.getDescription());
        assertFalse(dto.isOpen());
        assertEquals("Jane Doe", dto.getAuthorName());
        assertEquals(Collections.emptyList(), dto.getTagNames());
        assertEquals("http://test.image", dto.getMainImageUrl());
        assertEquals(EventRole.ORGANIZER, dto.getRole());
    }
}
