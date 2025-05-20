package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import greencity.dto.event.EventSearchDto;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventSearchResponseMapperTest {

    @InjectMocks
    private EventSearchResponseMapper mapper;

    @Test
    void convert_FromEventToEventSearchDto_success() {
        EventDateLocation eventDateLocation = EventDateLocation.builder()
            .startTime(ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES))
            .endTime(ZonedDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES))
            .build();

        Event event = Event.builder()
            .id(1L)
            .title("title")
            .description("description")
            .author(User.builder()
                .id(1L)
                .name("John Doe")
                .build())
            .eventDatesLocations(List.of(eventDateLocation))
            .build();

        EventSearchDto expected = EventSearchDto.builder()
            .id(1L)
            .title("title")
            .description("description")
            .organizer("John Doe")
            .startDate(ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES))
            .endDate(ZonedDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES))
            .build();

        EventSearchDto actual = mapper.convert(event);

        assertEquals(expected, actual);
    }
}
