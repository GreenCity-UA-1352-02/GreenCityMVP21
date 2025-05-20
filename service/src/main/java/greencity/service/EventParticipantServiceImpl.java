package greencity.service;

import greencity.dto.event.EventParticipantDto;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventParticipant;
import greencity.enums.EventRole;
import greencity.exception.exceptions.EntityNotFoundException;
import greencity.mapping.EventParticipantDtoMapper;
import greencity.repository.EventParticipantRepository;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventParticipantServiceImpl implements EventParticipantService {
    private final EventParticipantRepository participantRepository;
    private final UserRepo userRepository;

    /**
     * Retrieves the ID of the currently authenticated user based on the email stored in the security context.
     * Throws {@link EntityNotFoundException} if the user is not found in the database.
     *
     * @return the ID of the currently authenticated user.
     * @throws EntityNotFoundException if the user with the extracted email does not exist in the database.
     * @author Dmytro Kravchuk
     */
    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
        return user.getId();
    }

    /**
     * Retrieves all events associated with the currently authenticated user.
     * This includes events the user has created as an organizer and those they have joined as an attendee.
     * Ensures that the correct role is assigned for each event and updates roles if necessary.
     *
     * @return a list of {@link EventParticipantDto} representing the user's event participation's
     * @author Dmytro Kravchuk
     */
    @Override
    @Transactional
    public List<EventParticipantDto> getUserEvents() {
        Long userId = getCurrentUserId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Event> createdEvents = participantRepository.findAllEventsByAuthorId(userId);

        List<EventParticipant> userParticipations = participantRepository.findAllByUserIdAndActiveTrue(userId);

        Map<Long, EventParticipant> participationMap = userParticipations.stream()
            .collect(Collectors.toMap(ep -> ep.getEvent().getId(), ep -> ep));

        List<EventParticipant> allRelevantParticipations = new ArrayList<>();

        for (Event event : createdEvents) {
            EventParticipant ep = participationMap.get(event.getId());
            if (ep == null) {
                ep = EventParticipant.builder()
                    .user(user)
                    .event(event)
                    .role(EventRole.ORGANIZER)
                    .active(true)
                    .build();
                participantRepository.save(ep);
            } else if (ep.getRole() != EventRole.ORGANIZER) {
                ep.setRole(EventRole.ORGANIZER);
                participantRepository.save(ep);
            }
            allRelevantParticipations.add(ep);
        }

        for (EventParticipant ep : userParticipations) {
            Long eventId = ep.getEvent().getId();
            boolean isCreatedEvent = createdEvents.stream().anyMatch(e -> e.getId().equals(eventId));
            if (!isCreatedEvent) {
                if (ep.getRole() != EventRole.ATTENDEE) {
                    ep.setRole(EventRole.ATTENDEE);
                    participantRepository.save(ep);
                }
                allRelevantParticipations.add(ep);
            }
        }

        return allRelevantParticipations.stream()
            .map(ep -> EventParticipantDtoMapper.toDto(ep.getEvent(), ep.getRole()))
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all events associated with the currently authenticated user filtered by the given role.
     *
     * @param role the role (e.g., ORGANIZER, ATTENDEE) to filter user event participation's
     * @return a list of {@link EventParticipantDto} for the specified role of the user in events
     * @author Dmytro Kravchuk
     */
    @Override
    public List<EventParticipantDto> getUserEventsByRole(EventRole role) {
        Long userId = getCurrentUserId();
        return participantRepository.findAllByUserIdAndRoleAndActiveTrue(userId, role).stream()
            .map(ep -> EventParticipantDtoMapper.toDto(ep.getEvent(), ep.getRole()))
            .collect(Collectors.toList());
    }

    /**
     * Cancels the user's participation in the event with the given ID.
     * Sets the participation as inactive rather than deleting it from the database.
     *
     * @param eventId the ID of the event to cancel participation in
     * @throws EntityNotFoundException if no active participation is found for the user and event
     * @author Dmytro Kravchuk
     */
    @Override
    public void cancelParticipation(Long eventId) {
        Long userId = getCurrentUserId();
        EventParticipant participant = participantRepository.findByUserIdAndEventIdAndActiveTrue(userId, eventId)
            .orElseThrow(() -> new EntityNotFoundException("Participation not found"));
        participant.setActive(false);
        participantRepository.save(participant);
    }
}
