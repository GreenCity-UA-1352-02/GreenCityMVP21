package greencity.mapping;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.entity.NewsSubscriber;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NewsSubscriberMapper extends AbstractConverter<NewsSubscriberRequestDto, NewsSubscriber> {
    @Override
    public NewsSubscriber convert(NewsSubscriberRequestDto newsSubscriberRequestDto) {
        return NewsSubscriber.builder()
            .email(newsSubscriberRequestDto.email())
            .build();
    }
}
