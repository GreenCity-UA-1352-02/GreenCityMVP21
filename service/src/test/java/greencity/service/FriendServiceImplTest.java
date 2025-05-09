package greencity.service;

import greencity.dto.friend.FriendCardDto;
import greencity.dto.friend.FriendDto;
import greencity.dto.friend.FriendSearchRequest;
import greencity.entity.Friend;
import greencity.entity.User;
import greencity.entity.UserSpecification;
import greencity.enums.FriendStatus;
import greencity.exception.exceptions.FriendRequestException;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.FriendRepository;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendServiceImplTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private FriendRepository friendRepo;

    @InjectMocks
    private FriendServiceImpl friendService;

    private User currentUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("current@example.com");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("friend@example.com");

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser.getEmail(), null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getFriends_shouldReturnListOfFriends_whenUserExists_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        List<FriendDto> friendList = Arrays.asList(
            new FriendDto(2L, "Alice", "alice@example.com", "alice.jpg", "Kyiv"),
            new FriendDto(3L, "Bob", "bob@example.com", "bob.jpg", "Kyiv"));

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(friendRepo.findAllFriendsByUserId(userId)).thenReturn(friendList);

        List<FriendDto> result = friendService.getFriends(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
        assertThat(result.get(1).getName()).isEqualTo("Bob");

        verify(userRepo, times(1)).findById(userId);
        verify(friendRepo, times(1)).findAllFriendsByUserId(userId);
    }

    @Test
    void searchNewFriends_withMatchingSearchTerm_shouldReturnCorrectFriendCardDtos() {
        FriendSearchRequest request = new FriendSearchRequest(1L, "John", false,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(2L);
        user.setName("John Doe");
        user.setCity("Kyiv");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(2L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 2L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John Doe");
        assertThat(result.getContent().get(0).getFriendCount()).isEqualTo(2L);
        assertThat(result.getContent().get(0).getIsFriend()).isFalse();
    }

    @Test
    void searchNewFriends_withNoSearchMatches_shouldReturnEmptyPage() {
        FriendSearchRequest request = new FriendSearchRequest(1L, "Nonexistent", false,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(Page.empty(pageable));

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void searchNewFriends_whenFilterByCityIsEnabled_shouldFilterCorrectly() {
        FriendSearchRequest request = new FriendSearchRequest(1L, null, true,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(2L);
        user.setName("Alice");
        user.setCity("Kyiv");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(3L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 2L)).thenReturn(true);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getCity()).isEqualTo("Kyiv");
    }

    @Test
    void searchNewFriends_withMutualFriendsFilter_shouldReturnMutualFriends() {
        FriendSearchRequest request = new FriendSearchRequest(1L, null, false,
            true, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(3L);
        user.setName("Bob");
        user.setCity("Lviv");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(friendRepo.countByUserId(3L)).thenReturn(1L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 3L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Bob");
        assertThat(result.getContent().getFirst().getFriendCount()).isEqualTo(1L);
    }

    @Test
    void getFriends_shouldThrowUserNotFoundException_whenUserDoesNotExist_404NotFound() {
        Long userId = 1L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.getFriends(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User with id " + userId + " not found.");

        verify(userRepo, times(1)).findById(userId);
        verifyNoInteractions(friendRepo);
    }

    @Test
    void getFriends_shouldHandleUserWithNoFriends() {
        Long userId = 2L;
        User user = new User();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(friendRepo.findAllFriendsByUserId(userId)).thenReturn(List.of());

        List<FriendDto> result = friendService.getFriends(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(userRepo, times(1)).findById(userId);
        verify(friendRepo, times(1)).findAllFriendsByUserId(userId);
    }

    @Test
    void getFriends_shouldHandleUserWithMultipleFriends() {
        Long userId = 3L;
        User user = new User();
        user.setId(userId);

        List<FriendDto> friends = List.of(
            new FriendDto(4L, "David", "david@example.com", "david.jpg", "Kyiv"),
            new FriendDto(5L, "Eve", "eve@example.com", "eve.jpg", "Lviv")
        );

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(friendRepo.findAllFriendsByUserId(userId)).thenReturn(friends);

        List<FriendDto> result = friendService.getFriends(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("David");
        assertThat(result.get(1).getCity()).isEqualTo("Lviv");

        verify(userRepo, times(1)).findById(userId);
        verify(friendRepo, times(1)).findAllFriendsByUserId(userId);
    }

    @Test
    void searchNewFriends_shouldReturnFilteredByRating() {
        FriendSearchRequest request = new FriendSearchRequest(1L, null, null,
            null, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        User highRatedUser = new User();
        highRatedUser.setId(2L);
        highRatedUser.setName("Sophia");
        highRatedUser.setCity("Kyiv");
        highRatedUser.setRating(4.9);

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(highRatedUser), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(5L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 2L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Sophia");
        assertThat(result.getContent().getFirst().getRating()).isEqualTo(4.9);
    }

    @Test
    void searchNewFriends_whenCityIsProvidedAndFilterByCityFalse_shouldNotFilterByCity() {
        FriendSearchRequest request = new FriendSearchRequest(1L, null, false,
            null, "London", null);
        Pageable pageable = PageRequest.of(0, 5);

        User userInKyiv = new User();
        userInKyiv.setId(2L);
        userInKyiv.setName("Alice");
        userInKyiv.setCity("Kyiv");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(userInKyiv), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(2L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 2L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getCity()).isEqualTo("Kyiv");
    }

    @Test
    void searchNewFriends_withCityAndMutualFriendsFilter_shouldReturnFilteredResults() {
        FriendSearchRequest request = new FriendSearchRequest(1L, null, true,
            true, "Kyiv", null);
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(2L);
        user.setName("Eve");
        user.setCity("Kyiv");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(4L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 2L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Eve");
        assertThat(result.getContent().getFirst().getCity()).isEqualTo("Kyiv");
        assertThat(result.getContent().getFirst().getFriendCount()).isEqualTo(4L);
        assertThat(result.getContent().getFirst().getIsFriend()).isFalse();
    }

    @Test
    void searchNewFriends_withoutFilters_shouldReturnAllResults() {
        FriendSearchRequest request = new FriendSearchRequest(1L, null, false,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        User user1 = new User();
        user1.setId(2L);
        user1.setName("Alice");
        user1.setCity("Kyiv");

        User user2 = new User();
        user2.setId(3L);
        user2.setName("Bob");
        user2.setCity("Lviv");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user1, user2), pageable, 2));
        when(friendRepo.countByUserId(anyLong())).thenReturn(2L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(anyLong(), anyLong())).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Alice");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Bob");
    }

    @Test
    void searchNewFriends_withNullUserId_shouldThrowException() {
        FriendSearchRequest request = new FriendSearchRequest(null, "Test", false,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        assertThatThrownBy(() -> friendService.searchNewFriends(request, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must not be null");
    }

    @Test
    void searchNewFriends_whenFilterByCityIsNullAndCityIsProvided_shouldSetFilterByCityToFalse() {

        Long userId = 1L;
        FriendSearchRequest request = new FriendSearchRequest(userId, null, null,
            false, "Kyiv", null);
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(2L);
        user.setName("Alice");
        user.setCity("Kyiv");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(3L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(userId, 2L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(request.getFilterByCity()).isFalse();
    }

    @Test
    void searchNewFriends_whenUserIdIsNotNull_shouldProceedNormally() {
        FriendSearchRequest request = new FriendSearchRequest(1L, "Alice", false,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(2L);
        user.setName("Alice");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(1L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 2L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchNewFriends_whenUserIdIsNull_shouldThrowIllegalArgumentException() {
        FriendSearchRequest request = new FriendSearchRequest(null, "Alice", false,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        assertThatThrownBy(() -> friendService.searchNewFriends(request, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must not be null");
    }

    @Test
    void searchNewFriends_withValidUserId_shouldProceedWithoutError() {
        FriendSearchRequest request = new FriendSearchRequest(1L, "Test", false,
            false, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(2L);
        user.setName("Alice");

        when(userRepo.findAll(any(UserSpecification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(friendRepo.countByUserId(2L)).thenReturn(2L);
        when(friendRepo.existsByUserIdAndFriendIdAndStatus(1L, 2L)).thenReturn(false);

        Page<FriendCardDto> result = friendService.searchNewFriends(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Alice");
    }

    @Test
    void removeFriend_shouldRemoveFriendshipSuccessfully_whenFriendshipExists() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        String userEmail = "test@example.com";

        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setEmail(userEmail);

        User anotherUser = new User();
        anotherUser.setId(friendId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(currentUser));

        Friend friendship = new Friend();
        friendship.setId(1L);
        friendship.setUser(currentUser);
        friendship.setFriend(anotherUser);
        friendship.setStatus(FriendStatus.FRIEND);

        when(friendRepo.findByUserIdAndFriendId(currentUserId, friendId)).thenReturn(friendship);

        friendService.removeFriend(friendId);

        verify(friendRepo, times(1)).findByUserIdAndFriendId(currentUserId, friendId);
        verify(friendRepo, times(1)).delete(friendship);
    }

    @Test
    void removeFriend_shouldThrowException_whenFriendshipDoesNotExist() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(currentUserId, friendId)).thenReturn(null);

        assertThatThrownBy(() -> friendService.removeFriend(friendId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Friendship not found or you are not allowed to remove this friend.");

        verify(friendRepo, times(1)).findByUserIdAndFriendId(currentUserId, friendId);
        verify(friendRepo, never()).delete(any(Friend.class));
    }

    @Test
    void removeFriend_shouldHandleReverseFriendshipSuccessfully() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        Friend directFriendship = new Friend();
        directFriendship.setId(1L);
        directFriendship.setUser(currentUser);
        directFriendship.setFriend(anotherUser);
        directFriendship.setStatus(FriendStatus.FRIEND);

        Friend reverseFriendship = new Friend();
        reverseFriendship.setId(2L);
        reverseFriendship.setUser(anotherUser);
        reverseFriendship.setFriend(currentUser);
        reverseFriendship.setStatus(FriendStatus.FRIEND);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(currentUserId, friendId)).thenReturn(directFriendship);
        when(friendRepo.findByUserIdAndFriendId(friendId, currentUserId)).thenReturn(reverseFriendship);

        friendService.removeFriend(friendId);

        verify(friendRepo, times(1)).findByUserIdAndFriendId(currentUserId, friendId);
        verify(friendRepo, times(1)).findByUserIdAndFriendId(friendId, currentUserId);
        verify(friendRepo, times(1)).delete(directFriendship);
        verify(friendRepo, times(1)).delete(reverseFriendship);
    }

    @Test
    void removeFriend_shouldThrowException_whenDirectFriendshipIsNotFriend() {
        Long friendId = 2L;

        Friend directFriendship = new Friend();
        directFriendship.setUser(currentUser);
        directFriendship.setFriend(anotherUser);
        directFriendship.setStatus(FriendStatus.REQUESTED);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), friendId)).thenReturn(directFriendship);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> friendService.removeFriend(friendId));

        assertEquals("Friendship not found or you are not allowed to remove this friend.",
            exception.getMessage());
    }

    @Test
    void removeFriend_shouldHandleNullReverseFriendship() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        currentUser.setId(currentUserId);
        currentUser.setEmail("test@example.com");

        anotherUser.setId(friendId);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            currentUser.getEmail(), "password", new ArrayList<>());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        Friend directFriendship = new Friend();
        directFriendship.setId(1L);
        directFriendship.setUser(currentUser);
        directFriendship.setFriend(anotherUser);
        directFriendship.setStatus(FriendStatus.FRIEND);

        when(friendRepo.findByUserIdAndFriendId(currentUserId, friendId)).thenReturn(directFriendship);
        when(friendRepo.findByUserIdAndFriendId(friendId, currentUserId)).thenReturn(null);

        friendService.removeFriend(friendId);

        verify(friendRepo).delete(directFriendship);
        verify(friendRepo, never()).delete(argThat(friend -> friend != directFriendship));
    }

    @Test
    void removeFriend_shouldNotDeleteReverseIfNotFriendStatus() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        currentUser.setId(currentUserId);
        currentUser.setEmail("test@example.com");

        anotherUser.setId(friendId);
        anotherUser.setEmail("friend@example.com");

        Friend directFriendship = new Friend();
        directFriendship.setUser(currentUser);
        directFriendship.setFriend(anotherUser);
        directFriendship.setStatus(FriendStatus.FRIEND);

        Friend reverseFriendship = new Friend();
        reverseFriendship.setUser(anotherUser);
        reverseFriendship.setFriend(currentUser);
        reverseFriendship.setStatus(FriendStatus.REQUESTED);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            currentUser.getEmail(), "password", new ArrayList<>());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(currentUserId, friendId)).thenReturn(directFriendship);
        when(friendRepo.findByUserIdAndFriendId(friendId, currentUserId)).thenReturn(reverseFriendship);

        friendService.removeFriend(friendId);

        verify(friendRepo).delete(directFriendship);
        verify(friendRepo, never()).delete(reverseFriendship);
    }

    @Test
    void isCurrentUser_shouldReturnTrue_whenUserIdMatchesCurrentUser() {
        Long userId = 1L;
        String userEmail = "test@example.com";

        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setEmail(userEmail);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(currentUser));

        boolean result = friendService.checkIfCurrentUser(userId);

        assertTrue(result);
    }

    @Test
    void checkIfCurrentUser_shouldReturnTrue_whenUserIdMatchesCurrentUser() {
        Long userId = 1L;
        String userEmail = "test@example.com";

        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setEmail(userEmail);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(currentUser));

        boolean result = friendService.checkIfCurrentUser(userId);

        assertTrue(result);
    }

    @Test
    void checkIfCurrentUser_shouldReturnFalse_whenUserIdDoesNotMatchCurrentUser() {
        Long userId = 2L;
        String userEmail = "test@example.com";

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail(userEmail);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(currentUser));

        boolean result = friendService.checkIfCurrentUser(userId);

        assertFalse(result);
    }

    @Test
    void checkIfCurrentUser_shouldThrowException_whenCurrentUserNotFound() {
        Long userId = 1L;
        String userEmail = "test@example.com";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.checkIfCurrentUser(userId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Current user not found");
    }

    @Test
    void checkIfCurrentUser_shouldReturnFalse_whenUserIdIsNull() {
        Long userId = null;
        String userEmail = "test@example.com";

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail(userEmail);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(currentUser));

        boolean result = friendService.checkIfCurrentUser(userId);

        assertFalse(result);
    }

    @Test
    void confirmFriend_shouldThrowException_whenFriendIdIsNull() {
        assertThatThrownBy(() -> friendService.confirmFriend(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("RequesterId cannot be null.");
    }

    @Test
    void confirmFriend_shouldThrowException_whenCurrentUserNotFound() {
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.confirmFriend(anotherUser.getId()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Current user not found.");
    }

    @Test
    void confirmFriend_shouldThrowException_whenRequestNotFoundOrAlreadyConfirmed() {
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId()))
            .thenReturn(null);

        assertThatThrownBy(() -> friendService.confirmFriend(anotherUser.getId()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Friend request not found or already confirmed.");
    }

    @Test
    void confirmFriend_shouldUpdateStatusesAndSave_whenValidRequestProvided() {
        Friend friendRequest = new Friend();
        friendRequest.setUser(anotherUser);
        friendRequest.setFriend(currentUser);
        friendRequest.setStatus(FriendStatus.REQUESTED);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId()))
            .thenReturn(friendRequest);
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), anotherUser.getId()))
            .thenReturn(null);
        when(userRepo.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));

        friendService.confirmFriend(anotherUser.getId());

        assertThat(friendRequest.getStatus()).isEqualTo(FriendStatus.FRIEND);
        verify(friendRepo).save(friendRequest);

        ArgumentCaptor<Friend> captor = ArgumentCaptor.forClass(Friend.class);
        verify(friendRepo, times(2)).save(captor.capture());

        Friend savedReverse = captor.getAllValues().get(1);
        assertThat(savedReverse.getUser()).isEqualTo(currentUser);
        assertThat(savedReverse.getFriend()).isEqualTo(anotherUser);
        assertThat(savedReverse.getStatus()).isEqualTo(FriendStatus.FRIEND);
    }

    @Test
    void confirmFriend_shouldThrowException_whenRequestStatusIsNotRequested() {
        Friend friendRequest = new Friend();
        friendRequest.setUser(anotherUser);
        friendRequest.setFriend(currentUser);
        friendRequest.setStatus(FriendStatus.FRIEND);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId()))
            .thenReturn(friendRequest);

        assertThatThrownBy(() -> friendService.confirmFriend(anotherUser.getId()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Friend request not found or already confirmed.");
    }

    @Test
    void confirmFriend_shouldUpdateReverseFriendIfAlreadyExists() {
        Friend friendRequest = new Friend();
        friendRequest.setUser(anotherUser);
        friendRequest.setFriend(currentUser);
        friendRequest.setStatus(FriendStatus.REQUESTED);

        Friend reverseFriend = new Friend();
        reverseFriend.setUser(currentUser);
        reverseFriend.setFriend(anotherUser);
        reverseFriend.setStatus(FriendStatus.REQUESTED);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId()))
            .thenReturn(friendRequest);
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), anotherUser.getId()))
            .thenReturn(reverseFriend);

        friendService.confirmFriend(anotherUser.getId());

        assertEquals(FriendStatus.FRIEND, reverseFriend.getStatus());

        verify(friendRepo, times(2)).save(any(Friend.class));
    }

    @Test
    void declineFriend_shouldThrowException_whenFriendIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> friendService.declineFriend(null));
    }

    @Test
    void declineFriend_shouldThrowException_whenCurrentUserNotFound() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> friendService.declineFriend(anotherUser.getId()));
    }

    @Test
    void declineFriend_shouldThrowException_whenRequestIsNull() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> friendService.declineFriend(anotherUser.getId()));
    }

    @Test
    void declineFriend_shouldThrowException_whenRequestStatusIsNotRequested() {
        Friend request = new Friend(anotherUser, currentUser, FriendStatus.FRIEND);
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId())).thenReturn(request);

        assertThrows(RuntimeException.class, () -> friendService.declineFriend(anotherUser.getId()));
    }

    @Test
    void declineFriend_shouldDeleteRequest_whenValid() {
        Friend request = new Friend(anotherUser, currentUser, FriendStatus.REQUESTED);
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(anotherUser.getId(), currentUser.getId())).thenReturn(request);

        friendService.declineFriend(anotherUser.getId());

        verify(friendRepo).delete(request);
    }

    @Test
    void addFriend_shouldThrowException_ifUserIdNotCurrentUser() {
        Long userId = 99L;
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        assertThrows(IllegalStateException.class, () -> friendService.addFriend(userId, anotherUser.getId()));
    }

    @Test
    void addFriend_shouldThrowIllegalArgumentException_ifUserIdOrFriendIdNull() {
        assertThrows(IllegalArgumentException.class, () -> friendService.addFriend(null, 2L));
        assertThrows(IllegalArgumentException.class, () -> friendService.addFriend(1L, null));
    }

    @Test
    void addFriend_shouldThrowIllegalArgumentException_ifUserAndFriendAreSame() {
        assertThrows(IllegalArgumentException.class, () -> friendService.addFriend(1L, 1L));
    }

    @Test
    void addFriend_shouldThrowException_whenUsersAreAlreadyFriends() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        Friend existingRequest = new Friend();
        existingRequest.setStatus(FriendStatus.FRIEND);
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(existingRequest);

        assertThrows(FriendRequestException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldThrowException_ifRequestAlreadySent() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        Friend request = new Friend();
        request.setStatus(FriendStatus.REQUESTED);
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(request);

        assertThrows(FriendRequestException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldThrowException_ifRequestAlreadyReceived() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        Friend reverseRequest = new Friend();
        reverseRequest.setStatus(FriendStatus.REQUESTED);
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(reverseRequest);

        assertThrows(FriendRequestException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldThrowException_ifUserBlockedFriend() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        Friend block = new Friend();
        block.setStatus(FriendStatus.BLOCKED);
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(block);

        assertThrows(FriendRequestException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldThrowException_ifFriendBlockedUser() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);

        Friend block = new Friend();
        block.setStatus(FriendStatus.BLOCKED);
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(block);

        assertThrows(FriendRequestException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldSaveRequest_whenAllChecksPass() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(null);

        friendService.addFriend(1L, 2L);

        ArgumentCaptor<Friend> captor = ArgumentCaptor.forClass(Friend.class);
        verify(friendRepo).save(captor.capture());

        Friend savedRequest = captor.getValue();
        assertEquals(currentUser, savedRequest.getUser());
        assertEquals(anotherUser, savedRequest.getFriend());
        assertEquals(FriendStatus.REQUESTED, savedRequest.getStatus());
        assertEquals(anotherUser.getCity(), savedRequest.getCity());
    }

    @Test
    void addFriend_shouldThrowException_whenUserNotFound() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldThrowException_whenFriendNotFound() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldThrowException_whenFriendBlockedUser() {
        when(userRepo.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);

        Friend blocked = new Friend();
        blocked.setStatus(FriendStatus.BLOCKED);
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(blocked);

        assertThrows(FriendRequestException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriend_shouldThrowException_whenRequestAlreadySent() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser.getEmail(), null));

        when(userRepo.findByEmail(eq(currentUser.getEmail()))).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(eq(1L))).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(eq(2L))).thenReturn(Optional.of(anotherUser));
        when(friendRepo.findByUserIdAndFriendId(eq(1L), eq(2L)))
            .thenReturn(createFriendRequest(FriendStatus.REQUESTED));
        when(friendRepo.findByUserIdAndFriendId(eq(2L), eq(1L))).thenReturn(null);

        assertThrows(FriendRequestException.class, () -> friendService.addFriend(1L, 2L));
    }

    private Friend createFriendRequest(FriendStatus status) {
        Friend friend = new Friend();
        friend.setUser(currentUser);
        friend.setFriend(anotherUser);
        friend.setStatus(status);
        return friend;
    }

    @Test
    void getFriends_shouldThrowException_whenUserIdIsNull() {
        assertThatThrownBy(() -> friendService.getFriends(null))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User with id null not found.");
    }

    @Test
    void getFriends_shouldHandleEmptyFriendList() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));
        when(friendRepo.findAllFriendsByUserId(userId)).thenReturn(List.of());

        List<FriendDto> result = friendService.getFriends(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(userRepo).findById(userId);
        verify(friendRepo).findAllFriendsByUserId(userId);
    }

    @Test
    void getFriends_shouldReturnCorrectListOfFriends() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));

        List<FriendDto> friends = List.of(
            new FriendDto(2L, "Alice", "alice@example.com", "pic.jpg", "Kyiv"),
            new FriendDto(3L, "Bob", "bob@example.com", "pic2.jpg", "Lviv")
        );
        when(friendRepo.findAllFriendsByUserId(userId)).thenReturn(friends);

        List<FriendDto> result = friendService.getFriends(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
        assertThat(result.get(1).getName()).isEqualTo("Bob");

        verify(userRepo).findById(userId);
        verify(friendRepo).findAllFriendsByUserId(userId);
    }

    @Test
    void getFriends_shouldThrowException_whenUserNotFound() {
        Long userId = 1L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.getFriends(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User with id " + userId + " not found.");

        verify(userRepo).findById(userId);
        verifyNoInteractions(friendRepo);
    }

    @Test
    void checkForExistingRequest_shouldThrowException_whenRequestAlreadySent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser, null));

        Friend existingRequest = new Friend();
        existingRequest.setStatus(FriendStatus.REQUESTED);
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(existingRequest);

        var method = friendService.getClass().getDeclaredMethod("checkForExistingRequest", Long.class, Long.class);
        method.setAccessible(true);

        FriendRequestException thrown = assertThrows(FriendRequestException.class,
            () -> {
                try {
                    method.invoke(friendService, 1L, 2L);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        );

        assertEquals("You have already sent a friend request to this user.", thrown.getMessage());
    }

    @Test
    void checkForExistingRequest_shouldThrowException_whenRequestAlreadyReceived() throws Exception {
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser, null));

        Friend reverseRequest = new Friend();
        reverseRequest.setStatus(FriendStatus.REQUESTED);
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(reverseRequest);

        var method = friendService.getClass().getDeclaredMethod("checkForExistingRequest", Long.class, Long.class);
        method.setAccessible(true);

        FriendRequestException thrown = assertThrows(FriendRequestException.class, () -> {
            try {
                method.invoke(friendService, 1L, 2L);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertEquals("You have already received a friend request from this user.", thrown.getMessage());
    }

    @Test
    void checkForExistingRequest_shouldThrowException_whenAlreadyFriends() throws Exception {
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser, null));

        Friend existingRequest = new Friend();
        existingRequest.setStatus(FriendStatus.FRIEND);
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(existingRequest);

        var method = friendService.getClass().getDeclaredMethod("checkForExistingRequest", Long.class, Long.class);
        method.setAccessible(true);

        FriendRequestException thrown = assertThrows(FriendRequestException.class, () -> {
            try {
                method.invoke(friendService, 1L, 2L);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertEquals("You are already friends.", thrown.getMessage());
    }

    @Test
    void checkForExistingRequest_shouldNotThrowException_whenNoExistingRequest() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser, null));

        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(null);

        var method = friendService.getClass().getDeclaredMethod("checkForExistingRequest", Long.class, Long.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(friendService, 1L, 2L));
    }

    @Test
    void cancelFriendRequest_shouldCancelSuccessfully_whenRequestExists() {
        Long friendId = 2L;
        Friend request = new Friend();
        request.setUser(currentUser);
        request.setFriend(anotherUser);
        request.setStatus(FriendStatus.REQUESTED);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), friendId)).thenReturn(request);

        friendService.cancelFriendRequest(friendId);

        verify(friendRepo, times(1)).delete(request);
    }

    @Test
    void cancelFriendRequest_shouldThrowException_whenNoPendingRequestExists() {
        Long friendId = 2L;

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), friendId)).thenReturn(null);

        assertThrows(FriendRequestException.class, () -> friendService.cancelFriendRequest(friendId));
        verify(friendRepo, never()).delete(any(Friend.class));
    }

    @Test
    void cancelFriendRequest_shouldThrowException_whenRequestStatusIsNotRequested() {
        Long friendId = 2L;
        Friend request = new Friend();
        request.setUser(currentUser);
        request.setFriend(anotherUser);
        request.setStatus(FriendStatus.FRIEND);

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), friendId)).thenReturn(request);

        assertThrows(FriendRequestException.class, () -> friendService.cancelFriendRequest(friendId));
        verify(friendRepo, never()).delete(any(Friend.class));
    }

    @Test
    void blockUser_shouldThrowException_whenUserToBlockNotFound() {
        Long toBlockId = 2L;

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(toBlockId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> friendService.blockUser(toBlockId));
        verify(userRepo).findById(toBlockId);
        verifyNoInteractions(friendRepo);
    }

    @Test
    void blockUser_shouldThrowException_whenCurrentUserNotFound() {
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> friendService.blockUser(anotherUser.getId()));
        verifyNoInteractions(friendRepo);
    }

    @Test
    void blockUser_shouldSaveBlockedStatus_whenNoExistingFriendRelationship() {
        Long toBlockId = 2L;

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(toBlockId)).thenReturn(Optional.of(anotherUser));
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), toBlockId)).thenReturn(null);

        friendService.blockUser(toBlockId);

        ArgumentCaptor<Friend> captor = ArgumentCaptor.forClass(Friend.class);
        verify(friendRepo, times(1)).save(captor.capture());

        Friend savedBlock = captor.getValue();
        assertEquals(currentUser, savedBlock.getUser());
        assertEquals(anotherUser, savedBlock.getFriend());
        assertEquals(FriendStatus.BLOCKED, savedBlock.getStatus());
    }

    @Test
    void blockUser_shouldDeleteExistingRelationships_andThenBlockUser() {
        Long toBlockId = 2L;

        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(toBlockId)).thenReturn(Optional.of(anotherUser));

        Friend existingFriendship = new Friend(currentUser, anotherUser, FriendStatus.FRIEND);
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), toBlockId)).thenReturn(existingFriendship);

        friendService.blockUser(toBlockId);

        verify(friendRepo, times(1)).delete(existingFriendship);
        verify(friendRepo, times(1)).save(argThat(friend ->
            friend.getUser().equals(currentUser) &&
                friend.getFriend().equals(anotherUser) &&
                friend.getStatus() == FriendStatus.BLOCKED));
    }

    @Test
    void blockUser_shouldHandleReverseBlocking() {
        Long toBlockId = 2L;

        Friend reverseFriendship = new Friend(anotherUser, currentUser, FriendStatus.FRIEND);
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(toBlockId)).thenReturn(Optional.of(anotherUser));
        when(friendRepo.findByUserIdAndFriendId(currentUser.getId(), toBlockId)).thenReturn(null);
        when(friendRepo.findByUserIdAndFriendId(toBlockId, currentUser.getId())).thenReturn(reverseFriendship);

        friendService.blockUser(toBlockId);

        verify(friendRepo, times(1)).delete(reverseFriendship);
        verify(friendRepo, times(1)).save(argThat(friend ->
            friend.getUser().equals(currentUser) &&
                friend.getFriend().equals(anotherUser) &&
                friend.getStatus() == FriendStatus.BLOCKED));
    }

    @Test
    void checkForExistingRequest_shouldNotThrow_whenExistingRequestWithDifferentStatus() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser, null));

        Friend existingRequest = new Friend();
        existingRequest.setStatus(FriendStatus.BLOCKED); // Допустим, другой статус
        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(existingRequest);
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(null);

        var method = friendService.getClass().getDeclaredMethod("checkForExistingRequest", Long.class, Long.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(friendService, 1L, 2L));
    }

    @Test
    void checkForExistingRequest_shouldNotThrow_whenReverseRequestWithDifferentStatus() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(currentUser, null));

        when(friendRepo.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);

        Friend reverseRequest = new Friend();
        reverseRequest.setStatus(FriendStatus.BLOCKED); // Допустим, другой статус
        when(friendRepo.findByUserIdAndFriendId(2L, 1L)).thenReturn(reverseRequest);

        var method = friendService.getClass().getDeclaredMethod("checkForExistingRequest", Long.class, Long.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(friendService, 1L, 2L));
    }
}
