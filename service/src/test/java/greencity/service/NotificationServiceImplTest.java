package greencity.service;

import greencity.dto.notification.NotificationEvent;
import greencity.entity.Notification;
import greencity.enums.NotificationType;
import greencity.mapping.NotificationDtoMapper;
import greencity.mapping.NotificationEventMapper;
import greencity.repository.NotificationRepo;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationRepo notificationRepo;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationDtoMapper mapper;

    @Mock
    private NotificationEventMapper notificationEventMapper;

    private NotificationEvent notificationEvent = NotificationEvent.builder()
        .eventType(NotificationType.COMMENT_CREATED)
        .source("source")
        .timestamp(LocalDateTime.now())
        .targetUserId(2L)
        .build();

    @Test
    public void findNotificationById_existingNotification_success() {
        Long id = 1L;
        Notification notification = new Notification();
        notification.setId(id);

        when(notificationRepo.findById(id)).thenReturn(Optional.of(notification));
        when(mapper.convert(notification)).thenReturn(notificationEvent);

        assertEquals(notificationEvent, notificationService.findNotificationById(id));
        verify(notificationRepo, times(1)).findById(id);
    }

    @Test
    public void findNotificationById_notExistingNotification_throwsException() {
        Long id = 2L;

        when(notificationRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            notificationService.findNotificationById(id);
        });

        verify(notificationRepo, times(1)).findById(id);
    }

    @Test
    public void saveNotification_validNotificationEvent_success() {
        NotificationEvent notificationToSave = NotificationEvent.builder()
            .eventType(NotificationType.COMMENT_CREATED)
            .source("source")
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .targetUserId(2L)
            .build();

        Notification mappedEntity = new Notification();
        NotificationEvent mappedBackEvent = new NotificationEvent();
        Notification savedEntity = new Notification();

        when(notificationEventMapper.convert(notificationToSave)).thenReturn(mappedEntity);
        when(notificationRepo.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.convert(savedEntity)).thenReturn(mappedBackEvent);

        // Act
        NotificationEvent result = notificationService.saveNotification(notificationToSave);

        System.out.println(result);

        // Assert
        assertNotNull(result);
        assertEquals(mappedBackEvent, result);

        verify(notificationEventMapper).convert(notificationToSave);
        verify(notificationRepo).save(mappedEntity);
        verify(mapper).convert(savedEntity);
    }
}
