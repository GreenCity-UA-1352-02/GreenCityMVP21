package greencity.service;

import greencity.dto.notification.NotificationEvent;
import greencity.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducerService {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private static final String TOPIC = "notifications.greencity";
    private static final String SOURCE = "GREENCITY";
    private final NotificationService notificationService;

    /**
     * Sends a notification about a comment on an article.
     *
     * @param articleId       ID of the article
     * @param articleTitle    Title of the article
     * @param authorId        ID of the article author (who will receive the
     *                        notification)
     * @param commentatorId   ID of the user who commented
     * @param commentatorName Name of the user who commented
     */
    public void sendCommentNotification(Long articleId, String articleTitle,
        Long authorId, Long commentatorId, String commentatorName) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("actorId", commentatorId);
        payload.put("actorName", commentatorName);
        payload.put("articleId", articleId);
        payload.put("articleTitle", articleTitle);
        payload.put("objectType", "ARTICLE");

        NotificationEvent event = NotificationEvent.builder()
            .eventType(NotificationType.COMMENT_CREATED)
            .targetUserId(authorId)
            .source(SOURCE)
            .payload(payload)
            .timestamp(LocalDateTime.now())
            .build();

        kafkaTemplate.send(TOPIC, event);
        notificationService.saveNotification(event);
        log.info("Comment notification sent to Kafka for user {}", authorId);
    }

    /**
     * Sends a notification about a like on an article.
     *
     * @param articleId    ID of the article
     * @param articleTitle Title of the article
     * @param authorId     ID of the article author (who will receive the
     *                     notification)
     * @param likerId      ID of the user who liked
     * @param likerName    Name of the user who liked
     */
    public void sendLikeNotification(Long articleId, String articleTitle,
        Long authorId, Long likerId, String likerName) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("actorId", likerId);
        payload.put("actorName", likerName);
        payload.put("articleId", articleId);
        payload.put("articleTitle", articleTitle);
        payload.put("objectType", "ARTICLE");

        NotificationEvent event = NotificationEvent.builder()
            .eventType(NotificationType.ARTICLE_LIKED)
            .targetUserId(authorId)
            .source(SOURCE)
            .payload(payload)
            .timestamp(LocalDateTime.now())
            .build();

        kafkaTemplate.send(TOPIC, event);
        notificationService.saveNotification(event);
        log.info("Like notification sent to Kafka for user {}", authorId);
    }
}
