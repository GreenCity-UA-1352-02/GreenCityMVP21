package greencity.controller;

import greencity.dto.user.FriendDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendDto>> getFriends(@PathVariable Long userId) {
        List<FriendDto> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/{userId}/add/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            friendService.addFriend(userId, friendId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The request already exists or you are already friends.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/{userId}/confirm/{requesterId}")
    public ResponseEntity<?> confirmFriend(@PathVariable Long userId, @PathVariable Long requesterId) {
        try {
            friendService.confirmFriend(userId, requesterId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/block/{toBlockId}")
    public ResponseEntity<?> blockUser(@PathVariable Long userId, @PathVariable Long toBlockId) {
        try {
            friendService.blockUser(userId, toBlockId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/remove/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            friendService.removeFriend(userId, friendId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserVO>> searchNewFriends(
        @RequestParam String searchTerm,
        @RequestParam Long currentUserId
    ) {
        List<UserVO> result = friendService.searchNewFriends(searchTerm, currentUserId);
        return ResponseEntity.ok(result);
    }
}
