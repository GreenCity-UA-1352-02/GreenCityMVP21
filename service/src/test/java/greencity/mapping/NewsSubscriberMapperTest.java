package greencity.mapping;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.entity.NewsSubscriber;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.*;

class NewsSubscriberMapperTest {

    private final NewsSubscriberMapper newsSubscriberMapper = new NewsSubscriberMapper();

    @Test
    void convert_ValidEmail_NewsSubscriber() {
        NewsSubscriberRequestDto newsSubscriberRequestDto = new NewsSubscriberRequestDto(TEST_EMAIL);

        NewsSubscriber expected = NewsSubscriber.builder()
            .email(TEST_EMAIL)
            .build();
        var actual = newsSubscriberMapper.convert(newsSubscriberRequestDto);

        assertEquals(expected, actual);
    }

    @Test
    void convert_Null_NullPointerExceptionThrown() {
        NewsSubscriberRequestDto newsSubscriberRequestDto = null;

        assertThrows(NullPointerException.class, () -> newsSubscriberMapper.convert(newsSubscriberRequestDto));
    }
}