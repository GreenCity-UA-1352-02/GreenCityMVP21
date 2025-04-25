package greencity.entity.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity(name = "EventDateLocation")
@Table(name = "events_dates_locations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventDateLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ZonedDateTime startTime;

    @Column(nullable = false)
    private ZonedDateTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column
    private String offlinePlace;

    @Column
    private String onlineLink;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EventDateLocation that = (EventDateLocation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
