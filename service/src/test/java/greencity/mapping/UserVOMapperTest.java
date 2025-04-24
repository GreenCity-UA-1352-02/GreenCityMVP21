package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserVOMapperTest {
    @InjectMocks
    UserVOMapper mapper;

    @Test
    void convert() {
        UserVO expected = ModelUtils.getUserVOWithData();

        User userToBeConverted = User.builder()
            .id(expected.getId())
            .name(expected.getName())
            .email(expected.getEmail())
            .role(expected.getRole())
            .userCredo(expected.getUserCredo())
            .firstName(expected.getFirstName())
            .emailNotification(expected.getEmailNotification())
            .userStatus(expected.getUserStatus())
            .rating(expected.getRating())
            .verifyEmail(expected.getVerifyEmail() != null ? VerifyEmail.builder()
                .id(expected.getVerifyEmail().getId())
                .user(User.builder()
                    .id(expected.getVerifyEmail().getUser().getId())
                    .name(expected.getVerifyEmail().getUser().getName())
                    .build())
                .expiryDate(expected.getVerifyEmail().getExpiryDate())
                .token(expected.getVerifyEmail().getToken())
                .build() : null)
            .refreshTokenKey(expected.getRefreshTokenKey())
            .ownSecurity(expected.getOwnSecurity() != null ? OwnSecurity.builder()
                .id(expected.getOwnSecurity().getId())
                .password(expected.getOwnSecurity().getPassword())
                .user(User.builder()
                    .id(expected.getOwnSecurity().getUser().getId())
                    .email(expected.getOwnSecurity().getUser().getEmail())
                    .build())
                .build() : null)
            .dateOfRegistration(expected.getDateOfRegistration())
            .profilePicturePath(expected.getProfilePicturePath())
            .city(expected.getCity())
            .showShoppingList(expected.getShowShoppingList())
            .showEcoPlace(expected.getShowEcoPlace())
            .showLocation(expected.getShowLocation())
            .lastActivityTime(expected.getLastActivityTime())
            .build();

        UserVO actual = mapper.convert(userToBeConverted);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getRole(), actual.getRole());
        assertEquals(expected.getUserCredo(), actual.getUserCredo());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getEmailNotification(), actual.getEmailNotification());
        assertEquals(expected.getUserStatus(), actual.getUserStatus());
        assertEquals(expected.getRating(), actual.getRating());

        if (expected.getVerifyEmail() != null) {
            assertEquals(expected.getVerifyEmail().getId(), actual.getVerifyEmail().getId());
            assertEquals(expected.getVerifyEmail().getUser().getId(), actual.getVerifyEmail().getUser().getId());
            assertEquals(expected.getVerifyEmail().getUser().getName(), actual.getVerifyEmail().getUser().getName());
            assertEquals(expected.getVerifyEmail().getExpiryDate(), actual.getVerifyEmail().getExpiryDate());
            assertEquals(expected.getVerifyEmail().getToken(), actual.getVerifyEmail().getToken());
        } else {
            assertEquals(expected.getVerifyEmail(), actual.getVerifyEmail());
        }

        assertEquals(expected.getRefreshTokenKey(), actual.getRefreshTokenKey());

        if (expected.getOwnSecurity() != null) {
            assertEquals(expected.getOwnSecurity().getId(), actual.getOwnSecurity().getId());
            assertEquals(expected.getOwnSecurity().getPassword(), actual.getOwnSecurity().getPassword());
            assertEquals(expected.getOwnSecurity().getUser().getId(), actual.getOwnSecurity().getUser().getId());
            assertEquals(expected.getOwnSecurity().getUser().getEmail(), actual.getOwnSecurity().getUser().getEmail());
        } else {
            assertEquals(expected.getOwnSecurity(), actual.getOwnSecurity());
        }

        assertEquals(expected.getDateOfRegistration(), actual.getDateOfRegistration());
        assertEquals(expected.getProfilePicturePath(), actual.getProfilePicturePath());
        assertEquals(expected.getCity(), actual.getCity());
        assertEquals(expected.getShowShoppingList(), actual.getShowShoppingList());
        assertEquals(expected.getShowEcoPlace(), actual.getShowEcoPlace());
        assertEquals(expected.getShowLocation(), actual.getShowLocation());
    }
}