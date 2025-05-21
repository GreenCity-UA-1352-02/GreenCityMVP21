package greencity.service;

public interface NotificationProducerService {
    void sendCommentNotification(Long articleId, String articleTitle,
        Long authorId, Long commentatorId, String commentatorName);

    void sendLikeNotification(Long articleId, String articleTitle,
        Long authorId, Long likerId, String likerName);

    void sendCommentReplyNotification(Long articleId, String articleTitle, String objectType,
        Long commentAuthorId, Long replierId, String replierName);

    void sendCommentLikeNotification(Long articleId, String articleTitle, String objectType,
        Long commentAuthorId, Long likerId, String likerName);
}
