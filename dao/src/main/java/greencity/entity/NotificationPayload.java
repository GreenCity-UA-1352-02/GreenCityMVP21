package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notification_payload")
public class NotificationPayload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id")
    @NotNull
    private Long actorId;

    @Column(name = "actor_name")
    @NotNull
    private String actorName;

    @Column(name = "article_id")
    @NotNull
    private Long articleId;

    @Column(name = "article_title")
    private String articleTitle;

    @Column(name = "object_type")
    private String objectType;
}
