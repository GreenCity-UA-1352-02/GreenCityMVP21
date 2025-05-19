package greencity.entity.event;

import greencity.entity.Tag;
import greencity.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 70)
    @Column(name = "title", nullable = false, length = 70)
    private String title;

    @Size(min = 20, max = 63206)
    @Column(name = "description", nullable = false, length = 63206)
    private String description;

    @Column(name = "is_open", nullable = false)
    private boolean isOpen = true;

    @Size(max = 7)
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventDateLocation> eventDatesLocations = new ArrayList<>(7);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "event_tags",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, unique = true)
    private EventImage mainImage;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventImage> images = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Class<?> ooEffectiveClass =
            o instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != ooEffectiveClass) {
            return false;
        }
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}
