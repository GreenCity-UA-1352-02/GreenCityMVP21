package greencity.mapping;

import greencity.dto.notification.NotificationDtoRequest;
import greencity.entity.Notification;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoRequestMapper extends AbstractConverter<Notification, NotificationDtoRequest> {
    @Override
    public NotificationDtoRequest convert(Notification notification) {
        return NotificationDtoRequest.builder()
            .actorName(notification.getPayload().getActorName())
            .action(notification.getEventType().toString())
            .object(notification.getPayload().getArticleTitle())
            .timestamp(notification.getTimestamp()
                .truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();
    }
}
