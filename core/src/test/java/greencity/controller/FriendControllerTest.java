//package greencity.controller;
//
//import greencity.dto.friend.FriendDto;
//import greencity.exception.exceptions.UserNotFoundException;
//import greencity.service.FriendService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class FriendControllerTest {
//    @Mock
//    private FriendService friendService;
//
//    @InjectMocks
//    private FriendController friendController;
//
//    @Test
//    void getFriends_shouldReturnListOfFriends_whenUserExists_Success() {
//        Long userId = 1L;
//        List<FriendDto> friendList = Arrays.asList(
//            new FriendDto(2L, "Alice", "alice@example.com", "alice.jpg", "Kyiv"),
//            new FriendDto(3L, "Bob", "bob@example.com", "bob.jpg", "Kyiv"));
//
//        when(friendService.getFriends(userId)).thenReturn(friendList);
//
//        ResponseEntity<?> response = friendController.getFriends(userId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isInstanceOf(List.class);
//
//        @SuppressWarnings("unchecked")
//        List<FriendDto> body = (List<FriendDto>) response.getBody();
//        assertThat(body).hasSize(2);
//
//        FriendDto friend1 = body.get(0);
//        FriendDto friend2 = body.get(1);
//
//        assertThat(friend1.getId()).isEqualTo(2L);
//        assertThat(friend1.getName()).isEqualTo("Alice");
//        assertThat(friend1.getEmail()).isEqualTo("alice@example.com");
//        assertThat(friend1.getProfilePicture()).isEqualTo("alice.jpg");
//
//        assertThat(friend2.getId()).isEqualTo(3L);
//        assertThat(friend2.getName()).isEqualTo("Bob");
//        assertThat(friend2.getEmail()).isEqualTo("bob@example.com");
//        assertThat(friend2.getProfilePicture()).isEqualTo("bob.jpg");
//
//        verify(friendService, times(1)).getFriends(userId);
//    }
//
//    @Test
//    void getFriends_whenUserDoesNotExist_NotFound() {
//        Long userId = 1L;
//        String errorMessage = "User with id 1 not found";
//
//        when(friendService.getFriends(userId)).thenThrow(new UserNotFoundException(errorMessage));
//
//        ResponseEntity<?> response = friendController.getFriends(userId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//        assertThat(response.getBody()).isEqualTo(errorMessage);
//
//        verify(friendService, times(1)).getFriends(userId);
//    }
//
//    @Test
//    void addFriend_whenRequestIsSuccessful_201Created() {
//        Long userId = 1L;
//        Long friendId = 2L;
//
//        doNothing().when(friendService).addFriend(userId, friendId);
//
//        ResponseEntity<?> response = friendController.addFriend(userId, friendId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNull();
//
//        verify(friendService, times(1)).addFriend(userId, friendId);
//    }
//
//    @Test
//    void addFriend_whenRequestAlreadyExistsOrUsersAlreadyFriends_BadRequest() {
//        Long userId = 1L;
//        Long friendId = 2L;
//
//        doThrow(new IllegalArgumentException()).when(friendService).addFriend(userId, friendId);
//
//        ResponseEntity<?> response = friendController.addFriend(userId, friendId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).isEqualTo("The request already exists or you are already friends.");
//
//        verify(friendService, times(1)).addFriend(userId, friendId);
//    }
//
//    @Test
//    void addFriend_whenUserTriesToSendRequestOnBehalfOfAnotherUser_Forbidden() {
//        Long userId = 1L;
//        Long friendId = 2L;
//
//        String errorMessage = "You cannot send a friend request on behalf of another user.";
//
//        doThrow(new IllegalStateException(errorMessage)).when(friendService).addFriend(userId, friendId);
//
//        ResponseEntity<?> response = friendController.addFriend(userId, friendId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//        assertThat(response.getBody()).isEqualTo(errorMessage);
//
//        verify(friendService, times(1)).addFriend(userId, friendId);
//    }
//
//    @Test
//    void confirmFriend_whenConfirmationSuccessful_Success() {
//        Long userId = 1L;
//        Long requesterId = 2L;
//
//        doNothing().when(friendService).confirmFriend(userId, requesterId);
//
//        ResponseEntity<?> response = friendController.confirmFriend(userId, requesterId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNull();
//
//        verify(friendService, times(1)).confirmFriend(userId, requesterId);
//    }
//
//    @Test
//    void confirmFriend_whenRequestNotFoundOrAlreadyConfirmed_NotFound() {
//        Long userId = 1L;
//        Long requesterId = 2L;
//        String errorMessage = "Friend request not found or already confirmed.";
//
//        doThrow(new RuntimeException(errorMessage)).when(friendService).confirmFriend(userId, requesterId);
//
//        ResponseEntity<?> response = friendController.confirmFriend(userId, requesterId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//        assertThat(response.getBody()).isEqualTo(errorMessage);
//
//        verify(friendService, times(1)).confirmFriend(userId, requesterId);
//    }
//
//    @Test
//    void blockUser_whenBlockingSuccessful_Success() {
//        Long userId = 1L;
//        Long toBlockId = 2L;
//
//        doNothing().when(friendService).blockUser(userId, toBlockId);
//
//        ResponseEntity<?> response = friendController.blockUser(userId, toBlockId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNull();
//
//        verify(friendService, times(1)).blockUser(userId, toBlockId);
//    }
//
//    @Test
//    void blockUser_whenErrorOccursDuringBlocking_BadRequest() {
//        Long userId = 1L;
//        Long toBlockId = 2L;
//        String errorMessage = "Cannot block user due to some issue.";
//
//        doThrow(new RuntimeException(errorMessage)).when(friendService).blockUser(userId, toBlockId);
//
//        ResponseEntity<?> response = friendController.blockUser(userId, toBlockId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).isEqualTo(errorMessage);
//
//        verify(friendService, times(1)).blockUser(userId, toBlockId);
//    }
//
//    @Test
//    void removeFriend_whenRemovalSuccessful_NoContent() {
//        Long userId = 1L;
//        Long friendId = 2L;
//
//        doNothing().when(friendService).removeFriend(userId, friendId);
//
//        ResponseEntity<?> response = friendController.removeFriend(userId, friendId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//        assertThat(response.getBody()).isNull();
//
//        verify(friendService, times(1)).removeFriend(userId, friendId);
//    }
//
//    @Test
//    void removeFriend_whenFriendNotFound_NotFound() {
//        Long userId = 1L;
//        Long friendId = 2L;
//        String errorMessage = "Friendship not found.";
//
//        doThrow(new RuntimeException(errorMessage)).when(friendService).removeFriend(userId, friendId);
//
//        ResponseEntity<?> response = friendController.removeFriend(userId, friendId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//        assertThat(response.getBody()).isEqualTo(errorMessage);
//
//        verify(friendService, times(1)).removeFriend(userId, friendId);
//    }
//
//    @Test
//    void searchNewFriends_whenSearchSuccessful_Success() {
//        String searchTerm = "Al";
//        Long currentUserId = 1L;
//        List<FriendDto> foundFriends = Arrays.asList(
//            new FriendDto(2L, "Alice", "alice@example.com", "alice.jpg", "Kyiv"),
//            new FriendDto(3L, "Alex", "alex@example.com", "alex.jpg", "Kyiv"));
//
//        when(friendService.searchNewFriends(searchTerm, currentUserId)).thenReturn(foundFriends);
//
//        ResponseEntity<List<FriendDto>> response = friendController.searchNewFriends(searchTerm, currentUserId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody()).hasSize(2);
//
//        FriendDto friend1 = response.getBody().get(0);
//        FriendDto friend2 = response.getBody().get(1);
//
//        assertThat(friend1.getName()).isEqualTo("Alice");
//        assertThat(friend2.getName()).isEqualTo("Alex");
//
//        verify(friendService, times(1)).searchNewFriends(searchTerm, currentUserId);
//    }
//
//    @Test
//    void searchNewFriends_shouldReturnEmptyList_whenNoFriendsFound_Success() {
//        String searchTerm = "NonExistingName";
//        Long currentUserId = 1L;
//
//        when(friendService.searchNewFriends(searchTerm, currentUserId)).thenReturn(Collections.emptyList());
//
//        ResponseEntity<List<FriendDto>> response = friendController.searchNewFriends(searchTerm, currentUserId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody()).isEmpty();
//
//        verify(friendService, times(1)).searchNewFriends(searchTerm, currentUserId);
//    }
//
//}
