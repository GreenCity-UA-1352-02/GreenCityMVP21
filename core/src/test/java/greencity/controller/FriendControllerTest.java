package greencity.controller;

import greencity.dto.friend.FriendCardDto;
import greencity.dto.friend.FriendDto;
import greencity.dto.friend.FriendSearchRequest;
import greencity.exception.exceptions.FriendRequestException;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.service.FriendService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendControllerTest {
    @Mock
    private FriendService friendService;

    @InjectMocks
    private FriendController friendController;

    @Test
    void getFriends_shouldReturnListOfFriends_whenUserExists_Success() {
        Long userId = 1L;
        List<FriendDto> friendList = Arrays.asList(
            new FriendDto(2L, "Alice", "alice@example.com", "alice.jpg", "Kyiv"),
            new FriendDto(3L, "Bob", "bob@example.com", "bob.jpg", "Kyiv"));

        when(friendService.getFriends(userId)).thenReturn(friendList);

        ResponseEntity<?> response = friendController.getFriends(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<FriendDto> body = (List<FriendDto>) response.getBody();
        assertThat(body).hasSize(2);

        FriendDto friend1 = body.get(0);
        FriendDto friend2 = body.get(1);

        assertThat(friend1.getId()).isEqualTo(2L);
        assertThat(friend1.getName()).isEqualTo("Alice");
        assertThat(friend1.getEmail()).isEqualTo("alice@example.com");
        assertThat(friend1.getProfilePicture()).isEqualTo("alice.jpg");

        assertThat(friend2.getId()).isEqualTo(3L);
        assertThat(friend2.getName()).isEqualTo("Bob");
        assertThat(friend2.getEmail()).isEqualTo("bob@example.com");
        assertThat(friend2.getProfilePicture()).isEqualTo("bob.jpg");

        verify(friendService, times(1)).getFriends(userId);
    }

    @Test
    void getFriends_whenUserDoesNotExist_NotFound() {
        Long userId = 1L;
        String errorMessage = "User with id 1 not found";

        when(friendService.getFriends(userId)).thenThrow(new UserNotFoundException(errorMessage));

        ResponseEntity<?> response = friendController.getFriends(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(errorMessage);

        verify(friendService, times(1)).getFriends(userId);
    }

    @Test
    void addFriend_whenRequestIsSuccessful_201Created() {
        Long friendId = 2L;
        Long currentUserId = 1L;

        when(friendService.getCurrentUserId()).thenReturn(currentUserId);

        doNothing().when(friendService).addFriend(currentUserId, friendId);

        ResponseEntity<?> response = friendController.addFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Friend request sent successfully.");

        verify(friendService, times(1)).getCurrentUserId();
        verify(friendService, times(1)).addFriend(currentUserId, friendId);
    }

    @Test
    void addFriend_whenRequestAlreadyExistsOrUsersAlreadyFriends_BadRequest() {
        Long friendId = 2L;
        Long currentUserId = 1L;

        when(friendService.getCurrentUserId()).thenReturn(currentUserId);

        doThrow(new FriendRequestException("You have already sent a friend request to this user."))
            .when(friendService).addFriend(currentUserId, friendId);

        ResponseEntity<?> response = friendController.addFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("You have already sent a friend request to this user.");

        verify(friendService).getCurrentUserId();
        verify(friendService).addFriend(currentUserId, friendId);
    }

    @Test
    void addFriend_whenUserTriesToSendRequestOnBehalfOfAnotherUser_Forbidden() {
        Long friendId = 2L;
        Long currentUserId = 1L;

        String errorMessage = "You cannot send a friend request on behalf of another user.";

        when(friendService.getCurrentUserId()).thenReturn(currentUserId);
        doThrow(new IllegalStateException(errorMessage)).when(friendService).addFriend(currentUserId, friendId);

        ResponseEntity<?> response = friendController.addFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(errorMessage);

        verify(friendService).getCurrentUserId();
        verify(friendService).addFriend(currentUserId, friendId);
    }

    @Test
    void addFriend_whenUnexpectedErrorOccurs_InternalServerError() {
        Long friendId = 2L;
        Long currentUserId = 1L;

        String errorMessage = "An unexpected error occurred.";

        when(friendService.getCurrentUserId()).thenReturn(currentUserId);
        doThrow(new RuntimeException("Unexpected error")).when(friendService).addFriend(currentUserId, friendId);

        ResponseEntity<?> response = friendController.addFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(errorMessage);

        verify(friendService, times(1)).getCurrentUserId();
        verify(friendService, times(1)).addFriend(currentUserId, friendId);
    }

    @Test
    void addFriend_shouldReturnForbidden_whenTryingToAddYourself() {
        Long userId = 5L;

        when(friendService.getCurrentUserId()).thenReturn(userId);

        ResponseEntity<?> response = friendController.addFriend(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo("You cannot add yourself as a friend.");

        verify(friendService, times(1)).getCurrentUserId();
        verify(friendService, never()).addFriend(anyLong(), anyLong());
    }

    @Test
    void addFriend_shouldReturnCreated_whenFriendRequestSentSuccessfully() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        when(friendService.getCurrentUserId()).thenReturn(currentUserId);
        doNothing().when(friendService).addFriend(currentUserId, friendId);

        ResponseEntity<?> response = friendController.addFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Friend request sent successfully.");

        verify(friendService, times(1)).getCurrentUserId();
        verify(friendService, times(1)).addFriend(currentUserId, friendId);
    }

    @Test
    void confirmFriend_whenConfirmationSuccessful_Success() {
        Long friendId = 2L;

        doNothing().when(friendService).confirmFriend(friendId);

        ResponseEntity<?> response = friendController.confirmFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(friendService, times(1)).confirmFriend(friendId);
    }

    @Test
    void confirmFriend_whenRequestNotFoundOrAlreadyConfirmed_NotFound() {
        Long friendId = 2L;
        String errorMessage = "Friend request not found or already confirmed.";

        doThrow(new RuntimeException(errorMessage)).when(friendService).confirmFriend(friendId);

        ResponseEntity<?> response = friendController.confirmFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(errorMessage);

        verify(friendService, times(1)).confirmFriend(friendId);
    }

    @Test
    void blockUser_whenBlockingSuccessful_Success() {
        Long toBlockId = 2L;

        doNothing().when(friendService).blockUser(toBlockId);

        ResponseEntity<?> response = friendController.blockUser(toBlockId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(friendService, times(1)).blockUser(toBlockId);
    }

    @Test
    void blockUser_whenErrorOccursDuringBlocking_BadRequest() {
        Long toBlockId = 2L;
        String errorMessage = "Cannot block user due to some issue.";

        doThrow(new RuntimeException(errorMessage)).when(friendService).blockUser(toBlockId);

        ResponseEntity<?> response = friendController.blockUser(toBlockId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(errorMessage);

        verify(friendService, times(1)).blockUser(toBlockId);
    }

    @Test
    void removeFriend_whenRemovalSuccessful_NoContent() {
        Long friendId = 2L;

        doNothing().when(friendService).removeFriend(friendId);

        ResponseEntity<?> response = friendController.removeFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(response.getBody()).isNull();

        verify(friendService, times(1)).removeFriend(friendId);
    }

    @Test
    void removeFriend_whenFriendNotFound_NotFound() {
        Long friendId = 2L;
        String errorMessage = "Friendship not found.";

        doThrow(new RuntimeException(errorMessage)).when(friendService).removeFriend(friendId);

        ResponseEntity<?> response = friendController.removeFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(errorMessage);

        verify(friendService, times(1)).removeFriend(friendId);
    }

    @Test
    void searchNewFriends_whenSearchSuccessful_Success() {
        String searchTerm = "Al";
        Long currentUserId = 1L;
        String city = "Kyiv";
        Boolean filterByCity = true;
        Boolean filterByMutualFriends = false;
        Pageable pageable = PageRequest.of(0, 10);

        List<FriendCardDto> foundFriends = Arrays.asList(
            new FriendCardDto(2L, "Alice", "Kyiv", "alice.jpg", 4.5, 100L,
                true),
            new FriendCardDto(3L, "Alex", "Kyiv", "alex.jpg", 4.0, 80L,
                false)
        );
        Page<FriendCardDto> friendPage = new PageImpl<>(foundFriends, pageable, foundFriends.size());

        when(friendService.searchNewFriends(any(FriendSearchRequest.class), eq(pageable)))
            .thenReturn(friendPage);

        ResponseEntity<Page<FriendCardDto>> response = ResponseEntity.ok(friendController.searchNewFriends(
            currentUserId, searchTerm, filterByCity, filterByMutualFriends, city, null, pageable
        ));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(2);

        FriendCardDto friend1 = response.getBody().getContent().get(0);
        FriendCardDto friend2 = response.getBody().getContent().get(1);

        assertThat(friend1.getName()).isEqualTo("Alice");
        assertThat(friend1.getCity()).isEqualTo("Kyiv");
        assertThat(friend1.getRating()).isEqualTo(4.5);
        assertThat(friend1.getFriendCount()).isEqualTo(100L);
        assertThat(friend1.getIsFriend()).isTrue();

        assertThat(friend2.getName()).isEqualTo("Alex");
        assertThat(friend2.getCity()).isEqualTo("Kyiv");
        assertThat(friend2.getRating()).isEqualTo(4.0);
        assertThat(friend2.getFriendCount()).isEqualTo(80L);
        assertThat(friend2.getIsFriend()).isFalse();

        verify(friendService, times(1)).searchNewFriends(any(FriendSearchRequest.class),
            eq(pageable));
    }

    @Test
    void searchNewFriends_shouldReturnEmptyList_whenNoFriendsFound_Success() {
        String searchTerm = "NonExistingName";
        Long currentUserId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Page<FriendCardDto> emptyPage = Page.empty();

        when(friendService.searchNewFriends(any(FriendSearchRequest.class), eq(pageable)))
            .thenReturn(emptyPage);

        ResponseEntity<Page<FriendCardDto>> response = ResponseEntity.ok(
            friendController.searchNewFriends(
                currentUserId, searchTerm, null, null, null, null, pageable
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isEmpty();

        verify(friendService, times(1)).searchNewFriends(any(FriendSearchRequest.class), eq(pageable));
    }

    @Test
    void cancelFriendRequest_whenSuccessful_NoContent() {
        Long friendId = 2L;

        doNothing().when(friendService).cancelFriendRequest(friendId);

        ResponseEntity<?> response = friendController.cancelFriendRequest(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(friendService, times(1)).cancelFriendRequest(friendId);
    }

    @Test
    void cancelFriendRequest_whenNotFound_NotFound() {
        Long friendId = 2L;
        String errorMessage = "Friend request not found or already processed.";

        doThrow(new FriendRequestException(errorMessage)).when(friendService).cancelFriendRequest(friendId);

        ResponseEntity<?> response = friendController.cancelFriendRequest(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(errorMessage);

        verify(friendService, times(1)).cancelFriendRequest(friendId);
    }

    @Test
    void declineFriend_whenSuccessful_ReturnsOk() {
        Long friendId = 3L;

        doNothing().when(friendService).declineFriend(friendId);

        ResponseEntity<String> response = friendController.declineFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Friend request declined successfully.");

        verify(friendService, times(1)).declineFriend(friendId);
    }

    @Test
    void declineFriend_whenInvalidFriendId_ReturnsBadRequest() {
        Long friendId = 3L;
        String errorMessage = "Invalid friend ID.";

        doThrow(new IllegalArgumentException(errorMessage)).when(friendService).declineFriend(friendId);

        ResponseEntity<String> response = friendController.declineFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid friend ID.");

        verify(friendService, times(1)).declineFriend(friendId);
    }

    @Test
    void declineFriend_whenFriendRequestNotFound_ReturnsNotFound() {
        Long friendId = 3L;
        String errorMessage = "Friend request not found or already handled.";

        doThrow(new RuntimeException(errorMessage)).when(friendService).declineFriend(friendId);

        ResponseEntity<String> response = friendController.declineFriend(friendId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Friend request not found or already handled.");

        verify(friendService, times(1)).declineFriend(friendId);
    }
}
