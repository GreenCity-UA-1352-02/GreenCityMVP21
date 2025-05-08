//package greencity.service;
//
//import greencity.dto.friend.FriendDto;
//import greencity.entity.Friend;
//import greencity.entity.User;
//import greencity.enums.FriendStatus;
//import greencity.exception.exceptions.UserNotFoundException;
//import greencity.repository.FriendRepository;
//import greencity.repository.UserRepo;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class FriendServiceImplTest {
//    @Mock
//    private UserRepo userRepo;
//
//    @Mock
//    private FriendRepository friendRepo;
//
//    @InjectMocks
//    private FriendServiceImpl friendService;
//
//    private User currentUser;
//    private User anotherUser;
//
//    @BeforeEach
//    void setUp() {
//        currentUser = new User();
//        currentUser.setId(1L);
//        currentUser.setEmail("current@example.com");
//
//        anotherUser = new User();
//        anotherUser.setId(2L);
//        anotherUser.setEmail("friend@example.com");
//
//        SecurityContextHolder.getContext().setAuthentication(
//            new UsernamePasswordAuthenticationToken(currentUser.getEmail(), null));
//    }
//
//    @AfterEach
//    void clearSecurityContext() {
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    void getFriends_shouldReturnListOfFriends_whenUserExists_Success() {
//        Long userId = 1L;
//        User user = new User();
//        user.setId(userId);
//
//        List<FriendDto> friendList = Arrays.asList(
//            new FriendDto(2L, "Alice", "alice@example.com", "alice.jpg", "Kyiv"),
//            new FriendDto(3L, "Bob", "bob@example.com", "bob.jpg", "Kyiv"));
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
//        when(friendRepo.findAllFriendsByUserId(userId)).thenReturn(friendList);
//
//        List<FriendDto> result = friendService.getFriends(userId);
//
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getName()).isEqualTo("Alice");
//        assertThat(result.get(1).getName()).isEqualTo("Bob");
//
//        verify(userRepo, times(1)).findById(userId);
//        verify(friendRepo, times(1)).findAllFriendsByUserId(userId);
//    }
//
//    @Test
//    void getFriends_shouldThrowUserNotFoundException_whenUserDoesNotExist_404NotFound() {
//        Long userId = 1L;
//
//        when(userRepo.findById(userId)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> friendService.getFriends(userId))
//            .isInstanceOf(UserNotFoundException.class)
//            .hasMessageContaining("User with id " + userId + " not found.");
//
//        verify(userRepo, times(1)).findById(userId);
//        verifyNoInteractions(friendRepo);
//    }
//
//    @Test
//    void searchNewFriends_shouldReturnFilteredList_whenValidSearchTerm_Success() {
//        String searchTerm = "john";
//
//        User user1 = new User();
//        user1.setId(2L);
//        user1.setName("John Doe");
//        user1.setEmail("john@example.com");
//
//        User user2 = new User();
//        user2.setId(3L);
//        user2.setName("Jane Doe");
//        user2.setEmail("jane@example.com");
//
//        List<User> allMatches = List.of(user1, user2);
//
//        Friend friend = new Friend();
//        friend.setUser(currentUser);
//        friend.setFriend(user1);
//
//        List<Friend> existingRelations = List.of(friend);
//
//        when(userRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm))
//            .thenReturn(allMatches);
//        when(friendRepo.findAllByUserId(currentUser.getId()))
//            .thenReturn(existingRelations);
//
//        List<FriendDto> result = friendService.searchNewFriends(searchTerm, currentUser.getId());
//
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getId()).isEqualTo(3L);
//        assertThat(result.getFirst().getName()).isEqualTo("Jane Doe");
//    }
//
//    @Test
//    void searchNewFriends_shouldReturnEmptyList_whenAllUsersAreFriends_Success() {
//
//        Long currentUserId = 1L;
//        String searchTerm = "john";
//
//        User user1 = new User();
//        user1.setId(2L);
//        user1.setName("John Doe");
//        user1.setEmail("john@example.com");
//
//        List<User> allMatches = List.of(user1);
//
//        Friend friend = new Friend();
//        User currentUser = new User();
//        currentUser.setId(currentUserId);
//        friend.setUser(currentUser);
//        friend.setFriend(user1);
//
//        List<Friend> existingRelations = List.of(friend);
//
//        when(userRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm))
//            .thenReturn(allMatches);
//        when(friendRepo.findAllByUserId(currentUserId))
//            .thenReturn(existingRelations);
//
//        List<FriendDto> result = friendService.searchNewFriends(searchTerm, currentUserId);
//
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void searchNewFriends_shouldReturnEmptyList_whenNoMatchesFound() {
//
//        Long currentUserId = 1L;
//        String searchTerm = "nonexistent";
//
//        when(userRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm))
//            .thenReturn(List.of());
//        when(friendRepo.findAllByUserId(currentUserId))
//            .thenReturn(List.of());
//
//        List<FriendDto> result = friendService.searchNewFriends(searchTerm, currentUserId);
//
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void searchNewFriends_shouldNotReturnCurrentUser() {
//
//        Long currentUserId = 1L;
//        String searchTerm = "me";
//
//        User currentUser = new User();
//        currentUser.setId(currentUserId);
//        currentUser.setName("Me");
//        currentUser.setEmail("me@example.com");
//
//        List<User> allMatches = List.of(currentUser);
//
//        when(userRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm))
//            .thenReturn(allMatches);
//        when(friendRepo.findAllByUserId(currentUserId))
//            .thenReturn(List.of());
//
//        List<FriendDto> result = friendService.searchNewFriends(searchTerm, currentUserId);
//
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void addFriend_shouldAddFriendSuccessfully_whenValidData() {
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));
//        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId())).thenReturn(null);
//        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), anotherUser.getId())).thenReturn(null);
//
//        friendService.addFriend(currentUser.getId(), anotherUser.getId());
//
//        verify(friendRepo, times(1)).save(any(Friend.class));
//    }
//
//    @Test
//    void addFriend_shouldThrowException_whenUserAddingHimself() {
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//            friendService.addFriend(currentUser.getId(), currentUser.getId())
//        );
//
//        assertEquals("You cannot add yourself as a friend.", exception.getMessage());
//    }
//
//    @Test
//    void addFriend_shouldThrowException_whenCurrentUserMismatch() {
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
//            friendService.addFriend(999L, anotherUser.getId())
//        );
//
//        assertEquals("You cannot send friend requests on behalf of another user.", exception.getMessage());
//    }
//
//    @Test
//    void addFriend_shouldThrowException_whenUserNotFound() {
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//            friendService.addFriend(currentUser.getId(), anotherUser.getId())
//        );
//
//        assertEquals("User not found", exception.getMessage());
//    }
//
//    @Test
//    void addFriend_shouldThrowException_whenFriendNotFound() {
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(anotherUser.getId())).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//            friendService.addFriend(currentUser.getId(), anotherUser.getId())
//        );
//
//        assertEquals("Friend not found", exception.getMessage());
//    }
//
//    @Test
//    void addFriend_shouldThrowException_whenBlockedByFriend() {
//        Friend blocked = new Friend();
//        blocked.setStatus(FriendStatus.BLOCKED);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));
//        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId())).thenReturn(blocked);
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> friendService.addFriend(currentUser.getId(), anotherUser.getId()));
//
//        assertEquals("You have been blocked by this user.", exception.getMessage());
//    }
//
//    @Test
//    void addFriend_shouldThrowException_whenRequestAlreadyExists() {
//        Friend existing = new Friend();
//        existing.setStatus(FriendStatus.REQUESTED);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));
//        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId())).thenReturn(null);
//        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), anotherUser.getId())).thenReturn(existing);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> friendService.addFriend(currentUser.getId(), anotherUser.getId()));
//
//        assertEquals("The request already exists or you are already friends.", exception.getMessage());
//    }
//
//    @Test
//    void removeFriend_shouldRemoveBothDirections_whenBothExistAndAreFriends() {
//        Long userId = currentUser.getId();
//        Long friendId = anotherUser.getId();
//
//        Friend direct = new Friend();
//        direct.setUser(currentUser);
//        direct.setFriend(anotherUser);
//        direct.setStatus(FriendStatus.FRIEND);
//
//        Friend reverse = new Friend();
//        reverse.setUser(anotherUser);
//        reverse.setFriend(currentUser);
//        reverse.setStatus(FriendStatus.FRIEND);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(userId, friendId)).thenReturn(direct);
//        when(friendRepo.findByUserIdAndFriendId(friendId, userId)).thenReturn(reverse);
//
//        friendService.removeFriend(userId, friendId);
//
//        verify(friendRepo, times(1)).delete(direct);
//        verify(friendRepo, times(1)).delete(reverse);
//    }
//
//    @Test
//    void removeFriend_shouldRemoveOnlyDirect_whenReverseNotExist() {
//        Long userId = currentUser.getId();
//        Long friendId = anotherUser.getId();
//
//        Friend direct = new Friend();
//        direct.setUser(currentUser);
//        direct.setFriend(anotherUser);
//        direct.setStatus(FriendStatus.FRIEND);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(userId, friendId)).thenReturn(direct);
//        when(friendRepo.findByUserIdAndFriendId(friendId, userId)).thenReturn(null);
//
//        friendService.removeFriend(userId, friendId);
//
//        verify(friendRepo, times(1)).delete(direct);
//        verify(friendRepo, never()).delete(null);
//    }
//
//    @Test
//    void removeFriend_shouldThrowException_whenCurrentUserMismatch() {
//        Long otherUserId = 999L;
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//
//        RuntimeException exception =
//            assertThrows(RuntimeException.class, () -> friendService.removeFriend(otherUserId, anotherUser.getId()));
//
//        assertEquals("You can only remove your own friends.", exception.getMessage());
//    }
//
//    @Test
//    void removeFriend_shouldThrowException_whenNotFriends() {
//        Long userId = currentUser.getId();
//        Long friendId = anotherUser.getId();
//
//        Friend notFriend = new Friend();
//        notFriend.setUser(currentUser);
//        notFriend.setFriend(anotherUser);
//        notFriend.setStatus(FriendStatus.REQUESTED); // Не статус FRIEND
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(userId, friendId)).thenReturn(notFriend);
//
//        RuntimeException exception =
//            assertThrows(RuntimeException.class, () -> friendService.removeFriend(userId, friendId));
//
//        assertEquals("You are not friends.", exception.getMessage());
//    }
//
//    @Test
//    void removeFriend_shouldThrowException_whenDirectRelationNotFound() {
//        Long userId = currentUser.getId();
//        Long friendId = anotherUser.getId();
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(userId, friendId)).thenReturn(null);
//
//        RuntimeException exception =
//            assertThrows(RuntimeException.class, () -> friendService.removeFriend(userId, friendId));
//
//        assertEquals("You are not friends.", exception.getMessage());
//    }
//
//    @Test
//    void removeFriend_shouldThrowException_whenCurrentUserNotFound() {
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//            friendService.removeFriend(currentUser.getId(), anotherUser.getId())
//        );
//
//        assertEquals("Current user not found.", exception.getMessage());
//    }
//
//    @Test
//    void confirmFriend_shouldConfirmRequestAndCreateReverseFriendship_whenRequestIsValid() {
//        Long userId = currentUser.getId();
//        Long requesterId = anotherUser.getId();
//
//        Friend request = new Friend();
//        request.setUser(anotherUser);
//        request.setFriend(currentUser);
//        request.setStatus(FriendStatus.REQUESTED);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(requesterId, userId)).thenReturn(request);
//        when(userRepo.findById(requesterId)).thenReturn(Optional.of(anotherUser));
//
//        friendService.confirmFriend(userId, requesterId);
//
//        assertEquals(FriendStatus.FRIEND, request.getStatus());
//
//        ArgumentCaptor<Friend> argumentCaptor = ArgumentCaptor.forClass(Friend.class);
//
//        verify(friendRepo, times(2)).save(argumentCaptor.capture());
//
//        List<Friend> capturedFriends = argumentCaptor.getAllValues();
//
//        assertEquals(FriendStatus.FRIEND, capturedFriends.get(0).getStatus());
//        assertEquals(FriendStatus.FRIEND, capturedFriends.get(1).getStatus());
//
//        Friend reverse = capturedFriends.get(1);
//        assertEquals(userId, reverse.getUser().getId());
//        assertEquals(requesterId, reverse.getFriend().getId());
//    }
//
//    @Test
//    void confirmFriend_shouldThrowException_whenUserMismatch() {
//        Long invalidUserId = 999L;
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//
//        RuntimeException exception =
//            assertThrows(RuntimeException.class, () -> friendService.confirmFriend(invalidUserId, anotherUser.getId()));
//
//        assertEquals("Only the recipient of the friend request can confirm it.", exception.getMessage());
//    }
//
//    @Test
//    void confirmFriend_shouldThrowException_whenRequestNotFoundOrAlreadyConfirmed() {
//        Long userId = currentUser.getId();
//        Long requesterId = anotherUser.getId();
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(requesterId, userId)).thenReturn(null);
//
//        RuntimeException exception =
//            assertThrows(RuntimeException.class, () -> friendService.confirmFriend(userId, requesterId));
//
//        assertEquals("Friend request not found or already confirmed.", exception.getMessage());
//    }
//
//    @Test
//    void confirmFriend_shouldThrowException_whenCurrentUserNotFound() {
//        Long userId = currentUser.getId();
//        Long requesterId = anotherUser.getId();
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.empty());
//
//        RuntimeException exception =
//            assertThrows(RuntimeException.class, () -> friendService.confirmFriend(userId, requesterId));
//
//        assertEquals("Current user not found.", exception.getMessage());
//    }
//
//    @Test
//    void confirmFriend_shouldCreateFriendshipForRequester_whenRequestIsPending() {
//        Long userId = currentUser.getId();
//        Long requesterId = anotherUser.getId();
//
//        Friend request = new Friend();
//        request.setUser(anotherUser);
//        request.setFriend(currentUser);
//        request.setStatus(FriendStatus.REQUESTED);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(requesterId, userId)).thenReturn(request);
//        when(userRepo.findById(requesterId)).thenReturn(Optional.of(anotherUser));
//
//        friendService.confirmFriend(userId, requesterId);
//
//        assertEquals(FriendStatus.FRIEND, request.getStatus());
//
//        ArgumentCaptor<Friend> argumentCaptor = ArgumentCaptor.forClass(Friend.class);
//
//        verify(friendRepo, times(2)).save(argumentCaptor.capture());
//
//        List<Friend> capturedFriends = argumentCaptor.getAllValues();
//
//        Friend reverse = capturedFriends.get(1);
//        assertEquals(FriendStatus.FRIEND, reverse.getStatus());
//        assertEquals(userId, reverse.getUser().getId());
//        assertEquals(requesterId, reverse.getFriend().getId());
//    }
//
//    @Test
//    void confirmFriend_shouldThrowException_whenRequestAlreadyConfirmed() {
//        Long userId = currentUser.getId();
//        Long requesterId = anotherUser.getId();
//
//        Friend request = new Friend();
//        request.setUser(anotherUser);
//        request.setFriend(currentUser);
//        request.setStatus(FriendStatus.FRIEND);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(requesterId, userId)).thenReturn(request);
//
//        RuntimeException exception =
//            assertThrows(RuntimeException.class, () -> friendService.confirmFriend(userId, requesterId));
//
//        assertEquals("Friend request not found or already confirmed.", exception.getMessage());
//    }
//
//    @Test
//    void confirmFriend_shouldUpdateReverseFriendshipStatus_whenRequestIsValidAndReverseExists() {
//        Long userId = currentUser.getId();
//        Long requesterId = anotherUser.getId();
//
//        Friend request = new Friend();
//        request.setUser(anotherUser);
//        request.setFriend(currentUser);
//        request.setStatus(FriendStatus.REQUESTED);
//
//        Friend reverse = new Friend(currentUser, anotherUser, FriendStatus.REQUESTED);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(requesterId, userId)).thenReturn(request);
//        when(friendRepo.findByUserIdAndFriendId(userId, requesterId)).thenReturn(reverse);
//
//        friendService.confirmFriend(userId, requesterId);
//
//        assertEquals(FriendStatus.FRIEND, reverse.getStatus());
//
//        ArgumentCaptor<Friend> argumentCaptor = ArgumentCaptor.forClass(Friend.class);
//        verify(friendRepo, times(2)).save(argumentCaptor.capture());
//
//        List<Friend> capturedFriends = argumentCaptor.getAllValues();
//
//        assertEquals(FriendStatus.FRIEND, capturedFriends.get(0).getStatus());
//
//        Friend capturedReverse = capturedFriends.get(1);
//        assertEquals(FriendStatus.FRIEND, capturedReverse.getStatus());
//        assertEquals(userId, capturedReverse.getUser().getId());
//        assertEquals(requesterId, capturedReverse.getFriend().getId());
//    }
//
//    @Test
//    void blockUser_shouldBlockUserSuccessfully_whenNoExistingFriendship() {
//        Long userId = currentUser.getId();
//        Long toBlockId = anotherUser.getId();
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(toBlockId)).thenReturn(Optional.of(anotherUser));
//
//        when(friendRepo.findByUserIdAndFriendId(userId, toBlockId)).thenReturn(null);
//        when(friendRepo.findByUserIdAndFriendId(toBlockId, userId)).thenReturn(null);
//
//        friendService.blockUser(userId, toBlockId);
//
//        ArgumentCaptor<Friend> argumentCaptor = ArgumentCaptor.forClass(Friend.class);
//        verify(friendRepo, times(1)).save(argumentCaptor.capture());
//        Friend capturedFriend = argumentCaptor.getValue();
//        assertEquals(FriendStatus.BLOCKED, capturedFriend.getStatus());
//        assertEquals(userId, capturedFriend.getUser().getId());
//        assertEquals(toBlockId, capturedFriend.getFriend().getId());
//    }
//
//    @Test
//    void blockUser_shouldDeleteExistingFriendship_andBlockUser() {
//        Long userId = currentUser.getId();
//        Long toBlockId = anotherUser.getId();
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(toBlockId)).thenReturn(Optional.of(anotherUser));
//
//        Friend existingFriendship = new Friend(currentUser, anotherUser, FriendStatus.FRIEND);
//        Friend reverseFriendship = new Friend(anotherUser, currentUser, FriendStatus.FRIEND);
//        when(friendRepo.findByUserIdAndFriendId(userId, toBlockId)).thenReturn(existingFriendship);
//        when(friendRepo.findByUserIdAndFriendId(toBlockId, userId)).thenReturn(reverseFriendship);
//
//        friendService.blockUser(userId, toBlockId);
//
//        verify(friendRepo, times(1)).delete(existingFriendship);
//        verify(friendRepo, times(1)).delete(reverseFriendship);
//
//        ArgumentCaptor<Friend> argumentCaptor = ArgumentCaptor.forClass(Friend.class);
//        verify(friendRepo, times(1)).save(argumentCaptor.capture());
//        Friend capturedFriend = argumentCaptor.getValue();
//        assertEquals(FriendStatus.BLOCKED, capturedFriend.getStatus());
//        assertEquals(userId, capturedFriend.getUser().getId());
//        assertEquals(toBlockId, capturedFriend.getFriend().getId());
//    }
//
//    @Test
//    void blockUser_shouldThrowException_whenUserNotFound() {
//        Long userId = currentUser.getId();
//        Long toBlockId = anotherUser.getId();
//
//        when(userRepo.findById(userId)).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            friendService.blockUser(userId, toBlockId);
//        });
//
//        assertEquals("User not found", exception.getMessage());
//    }
//
//    @Test
//    void blockUser_shouldThrowException_whenUserToBlockNotFound() {
//        Long userId = currentUser.getId();
//        Long toBlockId = anotherUser.getId();
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(toBlockId)).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            friendService.blockUser(userId, toBlockId);
//        });
//
//        assertEquals("User to block not found", exception.getMessage());
//    }
//
//    @Test
//    void blockUser_shouldNotCreateDuplicateBlockedRelationship() {
//        Long userId = currentUser.getId();
//        Long toBlockId = anotherUser.getId();
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(toBlockId)).thenReturn(Optional.of(anotherUser));
//
//        Friend existingBlockedFriendship = new Friend(currentUser, anotherUser, FriendStatus.BLOCKED);
//        when(friendRepo.findByUserIdAndFriendId(userId, toBlockId)).thenReturn(existingBlockedFriendship);
//        when(friendRepo.findByUserIdAndFriendId(toBlockId, userId)).thenReturn(existingBlockedFriendship);
//
//        friendService.blockUser(userId, toBlockId);
//
//        verify(friendRepo, times(2)).delete(any(Friend.class));
//
//        verify(friendRepo, times(1)).save(any(Friend.class));
//    }
//
//    @Test
//    void removeFriend_shouldNotRemoveReverse_whenReverseStatusIsNotFriend() {
//        Long userId = currentUser.getId();
//        Long friendId = anotherUser.getId();
//
//        Friend direct = new Friend();
//        direct.setUser(currentUser);
//        direct.setFriend(anotherUser);
//        direct.setStatus(FriendStatus.FRIEND);
//
//        Friend reverse = new Friend();
//        reverse.setUser(anotherUser);
//        reverse.setFriend(currentUser);
//        reverse.setStatus(FriendStatus.REQUESTED);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(friendRepo.findByUserIdAndFriendId(userId, friendId)).thenReturn(direct);
//        when(friendRepo.findByUserIdAndFriendId(friendId, userId)).thenReturn(reverse);
//
//        friendService.removeFriend(userId, friendId);
//
//        verify(friendRepo, times(1)).delete(direct);
//        verify(friendRepo, never()).delete(reverse);
//    }
//
//    @Test
//    void addFriend_shouldContinue_whenBlockedEntryExistsButNotBlocked() {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn(currentUser.getEmail());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
//        when(userRepo.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));
//
//        Friend blockEntry = new Friend();
//        blockEntry.setStatus(FriendStatus.FRIEND);
//
//        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId())).thenReturn(blockEntry);
//
//        friendService.addFriend(currentUser.getId(), anotherUser.getId());
//
//        verify(friendRepo, times(1)).save(any(Friend.class));
//    }
//
//}
