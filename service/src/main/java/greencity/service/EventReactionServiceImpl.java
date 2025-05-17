package greencity.service;

import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventReaction;
import greencity.enums.ReactionType;
import greencity.exception.exceptions.OwnLikeError;
import greencity.repository.EventReactionRepository;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
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

    public long countLikes(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        return reactionRepository.countByEventAndReactionType(event, ReactionType.LIKE);
    }

    public long countDislikes(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        return reactionRepository.countByEventAndReactionType(event, ReactionType.DISLIKE);
    }
}
