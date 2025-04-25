package greencity.mapping;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewsSubscriberMapperTest {
    private final NewsSubscriberMapper newsSubscriberMapper = new NewsSubscriberMapper();

    @Test
    void convert_ValidEmail_NewsSubscriber() {
        NewsSubscriberRequestDto newsSubscriberRequestDto = new NewsSubscriberRequestDto(TEST_EMAIL);

        var newsSubscriber = newsSubscriberMapper.convert(newsSubscriberRequestDto);

        assertEquals(TEST_EMAIL, newsSubscriber.getEmail());
    }

    @Test
    void convert_Null_NullPointerExceptionThrown() {
        NewsSubscriberRequestDto newsSubscriberRequestDto = null;

        assertThrows(NullPointerException.class, () -> newsSubscriberMapper.convert(newsSubscriberRequestDto));
    }
}