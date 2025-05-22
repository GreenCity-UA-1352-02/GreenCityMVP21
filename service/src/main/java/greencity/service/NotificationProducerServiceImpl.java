package greencity.service;

import greencity.dto.notification.NotificationEvent;
import greencity.dto.notification.NotificationPayloadDto;
import greencity.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducerServiceImpl implements NotificationProducerService {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private static final String TOPIC = "notifications.greencity";
    private static final String SOURCE = "GREENCITY";
    private final NotificationService notificationService;

    /**
     * Universal method for sending notifications.
     *
     * @param notificationType Type of notification (COMMENT_CREATED, ARTICLE_LIKED,
     *                         etc.)
     * @param targetUserId     ID of the user who will receive the notification
     * @param actorId          ID of the user who performed the action
     * @param actorName        Name of the user who performed the action
     * @param articleId        ID of the article
     * @param articleTitle     Title of the article
     * @param objectType       Type of the object (ARTICLE, EVENT)
     */
    private void sendNotification(NotificationType notificationType,
        Long targetUserId,
        Long actorId,
        String actorName,
        Long articleId,
        String articleTitle,
        String objectType) {
        NotificationPayloadDto notificationPayload = NotificationPayloadDto.builder()
            .actorId(actorId)
            .actorName(actorName)
            .articleId(articleId)
            .articleTitle(articleTitle)
            .objectType(objectType)
            .build();

        NotificationEvent event = NotificationEvent.builder()
            .eventType(notificationType)
            .targetUserId(targetUserId)
            .source(SOURCE)
            .payload(notificationPayload)
            .timestamp(LocalDateTime.now())
            .build();

        kafkaTemplate.send(TOPIC, event);
        notificationService.saveNotification(event);
        log.info("{} notification sent to Kafka for user {}", notificationType, targetUserId);
    }

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
        sendNotification(
            NotificationType.COMMENT_CREATED,
            authorId,
            commentatorId,
            commentatorName,
            articleId,
            articleTitle,
            "ARTICLE");
    }

    /**
     * Sends a notification about a comment on an article or other object.
     *
     * @param articleId       ID of the article or object where the comment was made
     * @param articleTitle    Title of the article or object where the comment was made
     * @param authorId        ID of the article or object author (who will receive the notification)
     * @param commentatorId   ID of the user who made the comment
     * @param commentatorName Name of the user who made the comment
     * @param objectType      Type of the object (e.g., "ARTICLE", "EVENT") where the comment was made
     */
    public void sendCommentNotification(Long articleId, String articleTitle,
                                        Long authorId, Long commentatorId, String commentatorName, String objectType) {
        sendNotification(
                NotificationType.COMMENT_CREATED,
                authorId,
                commentatorId,
                commentatorName,
                articleId,
                articleTitle,
                objectType);
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
        sendNotification(
            NotificationType.ARTICLE_LIKED,
            authorId,
            likerId,
            likerName,
            articleId,
            articleTitle,
            "ARTICLE");
    }


    /**
     * Sends a notification about a like on an article or other*/
    public void sendLikeNotification(Long articleId, String articleTitle,
                                     Long authorId, Long likerId, String likerName, String objectType) {
        sendNotification(
                NotificationType.ARTICLE_LIKED,
                authorId,
                likerId,
                likerName,
                articleId,
                articleTitle,
                objectType);
    }

    /**
     * Sends a notification about a reply to a comment.
     *
     * @param articleId       ID of the article
     * @param articleTitle    Title of the article
     * @param objectType      Type of the object (ARTICLE, EVENT)
     * @param commentAuthorId ID of the comment author (who will receive the
     *                        notification)
     * @param replierId       ID of the user who replied
     * @param replierName     Name of the user who replied
     */
    public void sendCommentReplyNotification(Long articleId, String articleTitle, String objectType,
        Long commentAuthorId, Long replierId, String replierName) {
        sendNotification(
            NotificationType.COMMENT_REPLIED,
            commentAuthorId,
            replierId,
            replierName,
            articleId,
            articleTitle,
            objectType);
    }

    /**
     * Sends a notification about a like on a comment.
     *
     * @param articleId       ID of the article
     * @param articleTitle    Title of the article
     * @param objectType      Type of the object (ARTICLE, EVENT)
     * @param commentAuthorId ID of the comment author (who will receive the
     *                        notification)
     * @param likerId         ID of the user who liked
     * @param likerName       Name of the user who liked
     */
    public void sendCommentLikeNotification(Long articleId, String articleTitle, String objectType,
        Long commentAuthorId, Long likerId, String likerName) {
        sendNotification(
            NotificationType.COMMENT_LIKED,
            commentAuthorId,
            likerId,
            likerName,
            articleId,
            articleTitle,
            objectType);
    }
}
