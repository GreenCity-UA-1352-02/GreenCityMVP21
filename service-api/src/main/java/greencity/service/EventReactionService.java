package greencity.service;

import greencity.enums.ReactionType;

public interface EventReactionService {
    void react(long eventId, ReactionType type);

    long countLikes(Long eventId);

    long countDislikes(Long eventId);
}
