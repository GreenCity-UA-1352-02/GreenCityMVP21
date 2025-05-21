package greencity.service;

import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventReaction;
import greencity.enums.ReactionType;
import greencity.exception.exceptions.EntityNotFoundException;
import greencity.exception.exceptions.OwnLikeError;
import greencity.repository.EventReactionRepository;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventReactionServiceImpl implements EventReactionService {
    private final EventReactionRepository reactionRepository;
    private final UserRepo userRepository;
    private final EventRepo eventRepository;

    /**
     * Handles user reactions (like or dislike) to an event.
     *
     * <p>
     * If a user reacts to their own event, an {@link OwnLikeError} is thrown.
     * If the same reaction already exists, it will be removed.
     * If the reaction exists but with a different type, it will be updated.
     * Otherwise, a new reaction will be saved.
     * </p>
     *
     * @param eventId ID of the event to react to
     * @param type    Type of reaction ({@link ReactionType#LIKE} or {@link ReactionType#DISLIKE})
     * @throws greencity.exception.exceptions.EntityNotFoundException if the event is not found
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if the user is not found
     * @throws OwnLikeError if the user tries to react to their own event
     *
     * @author Dmytro Kravchuk
     */
    @Override
    public void react(long eventId, ReactionType type) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (event.getAuthor().getId().equals(user.getId())) {
            throw new OwnLikeError("Cannot react to your own event");
        }

        Optional<EventReaction> existingReaction = reactionRepository.findByUserIdAndEventId(user.getId(), eventId);

        if (existingReaction.isPresent()) {
            EventReaction reaction = existingReaction.get();
            if (reaction.getReactionType() == type) {
                reactionRepository.delete(reaction);
            } else {
                reaction.setReactionType(type);
                reaction.setCreatedDate(LocalDateTime.now());
                reactionRepository.save(reaction);
            }
        } else {
            EventReaction reaction = EventReaction.builder()
                .user(user)
                .event(event)
                .reactionType(type)
                .createdDate(LocalDateTime.now())
                .build();
            reactionRepository.save(reaction);
        }
    }

    /**
     * Counts the number of likes for a given event.
     *
     * <p>
     * Retrieves the event by its ID. If the event is not found,
     * an {@link greencity.exception.exceptions.EntityNotFoundException} is thrown.
     * Returns the total number of reactions of type {@link ReactionType#LIKE}
     * associated with the specified event.
     * </p>
     *
     * @param eventId the ID of the event for which likes are to be counted
     * @return the number of likes for the specified event
     * @throws greencity.exception.exceptions.EntityNotFoundException if the event is not found
     *
     * @author Dmytro Kravchuk
     */
    public long countLikes(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }
        return reactionRepository.countByEventIdAndReactionType(eventId, ReactionType.LIKE);
    }

    /**
     * Counts the number of dislikes for a given event.
     *
     * <p>
     * Retrieves the event by its ID. If the event does not exist,
     * a {@link greencity.exception.exceptions.EntityNotFoundException} is thrown.
     * Returns the total number of reactions of type {@link ReactionType#DISLIKE}
     * associated with the specified event.
     * </p>
     *
     * @param eventId the ID of the event for which dislikes are to be counted
     * @return the number of dislikes for the specified event
     * @throws greencity.exception.exceptions.EntityNotFoundException if the event is not found
     *
     * @author Dmytro Kravchuk
     */
    public long countDislikes(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }
        return reactionRepository.countByEventIdAndReactionType(eventId, ReactionType.DISLIKE);
    }
}
