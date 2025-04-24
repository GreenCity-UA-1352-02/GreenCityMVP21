package greencity.entity;

import greencity.enums.EventVisibility;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"dateDetails", "initiativeTypes", "images", "mainImage"})
@EqualsAndHashCode(exclude = {"dateDetails", "initiativeTypes", "images", "mainImage"})
@EntityListeners(AuditingEntityListener.class)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 70)
    private String title;

    @Column(name = "description", nullable = false, length = 63206)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_visability", nullable = false)
    private EventVisibility visibility;

    @OneToMany(
        mappedBy = "event",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<EventDateDetails> dateDetails = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "event_tags",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> initiativeTypes;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "main_image_id", nullable = false)
    private EventImage mainImage;

    @OneToMany(
        mappedBy = "event",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<EventImage> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "event_participants",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> participants = new ArrayList<>();
}
