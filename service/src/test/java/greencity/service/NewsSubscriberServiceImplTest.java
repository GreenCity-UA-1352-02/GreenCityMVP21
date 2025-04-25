package greencity.service;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
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

        when(newsSubscriberRepo.existsByEmail(newsSubscriberRequestDto.email())).thenReturn(false);

        assertEquals(newsSubscriberRequestDto, newsSubscriberService.subscribe(newsSubscriberRequestDto));

        verify(newsSubscriberRepo, times(1)).existsByEmail(newsSubscriberRequestDto.email());
        verify(newsSubscriberRepo, times(1)).save(any());
    }

    @Test
    void subscribe_EmailExist_NewsSubscriberRequestDto() {
        NewsSubscriberRequestDto newsSubscriberRequestDto = new NewsSubscriberRequestDto(TEST_EMAIL);

        when(newsSubscriberRepo.existsByEmail(newsSubscriberRequestDto.email())).thenReturn(true);

        assertEquals(newsSubscriberRequestDto, newsSubscriberService.subscribe(newsSubscriberRequestDto));

        verify(newsSubscriberRepo, times(1)).existsByEmail(newsSubscriberRequestDto.email());
        verify(newsSubscriberRepo, never()).save(any());
    }
}