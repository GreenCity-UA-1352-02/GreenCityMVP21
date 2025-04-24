package greencity.entity;

import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.*;

@Entity
@Table(name = "event_date_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"event"})
@ToString(exclude = {"event"})
public class EventDateDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean allDay;

    @Column
    private boolean isOnline;

    @Column
    private boolean isPlace;

    @Column
    private String offlinePlace;

    @Column
    private String onlinePlace;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
