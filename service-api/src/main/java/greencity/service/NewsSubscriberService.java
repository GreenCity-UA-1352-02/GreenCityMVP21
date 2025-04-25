package greencity.service;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;

public interface NewsSubscriberService {
    /**
     * Method for subscribing to the news.
     *
     * @param newsSubscriberRequestDto - email of the subscriber
     * @return {@link NewsSubscriberRequestDto} - email of the subscriber
     */
    NewsSubscriberRequestDto subscribe(NewsSubscriberRequestDto newsSubscriberRequestDto);
}
