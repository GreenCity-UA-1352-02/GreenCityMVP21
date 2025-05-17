package greencity.controller;

import greencity.enums.ReactionType;
import greencity.service.EventReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events/reactions")
public class EventReactionController {
    private final EventReactionService reactionService;

    public EventReactionController(EventReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping("/{eventId}/like")
    public ResponseEntity<Void> likeEvent(@PathVariable Long eventId) {
        reactionService.react(eventId, ReactionType.LIKE);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/dislike")
    public ResponseEntity<Void> dislikeEvent(@PathVariable Long eventId) {
        reactionService.react(eventId, ReactionType.DISLIKE);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{eventId}/likes")
    public ResponseEntity<Long> getLikes(@PathVariable Long eventId) {
        return ResponseEntity.ok(reactionService.countLikes(eventId));
    }

    @GetMapping("/{eventId}/dislikes")
    public ResponseEntity<Long> getDislikes(@PathVariable Long eventId) {
        return ResponseEntity.ok(reactionService.countDislikes(eventId));
    }
}
