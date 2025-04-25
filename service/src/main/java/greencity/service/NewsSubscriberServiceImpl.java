package greencity.service;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.entity.NewsSubscriber;
import greencity.repository.NewsSubscriberRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsSubscriberServiceImpl implements NewsSubscriberService {
    private final NewsSubscriberRepo newsSubscriberRepo;
    private final ModelMapper modelMapper;

    @Override
    public NewsSubscriberRequestDto subscribe(NewsSubscriberRequestDto newsSubscriberRequestDto) {
        if (newsSubscriberRepo.existsByEmail(newsSubscriberRequestDto.email())) {
            return newsSubscriberRequestDto;
        }
        newsSubscriberRepo.save(modelMapper.map(newsSubscriberRequestDto, NewsSubscriber.class));
        return newsSubscriberRequestDto;
    }
}
