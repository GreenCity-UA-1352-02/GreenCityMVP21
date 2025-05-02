package greencity.entity;

import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private NotificationType eventType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payload_id", referencedColumnName = "id")
    private NotificationPayload payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status = NotificationStatus.UNREAD;
    
    @Column(name = "timestamp_deletion")
    private LocalDateTime timestampDeletion;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = NotificationStatus.UNREAD;
        }
    }
}
