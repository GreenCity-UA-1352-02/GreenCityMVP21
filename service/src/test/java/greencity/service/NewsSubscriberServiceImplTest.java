package greencity.service;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.entity.NewsSubscriber;
import greencity.repository.NewsSubscriberRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static greencity.ModelUtils.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsSubscriberServiceImplTest {
    @Mock
    private NewsSubscriberRepo newsSubscriberRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NewsSubscriberServiceImpl newsSubscriberService;

    @Test
    void subscribe_EmailNotExist_NewsSubscriberRequestDto() {
        NewsSubscriberRequestDto newsSubscriberRequestDto = new NewsSubscriberRequestDto(TEST_EMAIL);
        NewsSubscriber newsSubscriber = NewsSubscriber.builder()
            .email(TEST_EMAIL)
            .build();

        when(newsSubscriberRepo.existsByEmail(newsSubscriberRequestDto.email())).thenReturn(false);
        when(modelMapper.map(newsSubscriberRequestDto, NewsSubscriber.class)).thenReturn(newsSubscriber);
        when(newsSubscriberRepo.save(newsSubscriber)).thenReturn(newsSubscriber);

        assertEquals(newsSubscriberRequestDto, newsSubscriberService.subscribe(newsSubscriberRequestDto));

        verify(newsSubscriberRepo, times(1)).existsByEmail(newsSubscriberRequestDto.email());
        verify(modelMapper, times(1)).map(newsSubscriberRequestDto, NewsSubscriber.class);
        verify(newsSubscriberRepo, times(1)).save(newsSubscriber);
    }

    @Test
    void subscribe_EmailExist_NewsSubscriberRequestDto() {
        NewsSubscriberRequestDto newsSubscriberRequestDto = new NewsSubscriberRequestDto(TEST_EMAIL);

        when(newsSubscriberRepo.existsByEmail(newsSubscriberRequestDto.email())).thenReturn(true);

        assertEquals(newsSubscriberRequestDto, newsSubscriberService.subscribe(newsSubscriberRequestDto));

        verify(newsSubscriberRepo, times(1)).existsByEmail(newsSubscriberRequestDto.email());
        verify(newsSubscriberRepo, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }
}