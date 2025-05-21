package greencity.mapping;

import greencity.dto.habit.HabitVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.Habit;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HabitVOMapper extends AbstractConverter<Habit, HabitVO> {
    @Override
    protected HabitVO convert(Habit habit) {
        return HabitVO.builder()
                .id(habit.getId())
                .image(habit.getImage())
                .complexity(habit.getComplexity())
                .defaultDuration(habit.getDefaultDuration())
                .usersLiked(habit.getUsersLiked().stream()
                        .map(user -> UserVO.builder().id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .userCredo(user.getUserCredo())
                                .firstName(user.getFirstName())
                                .emailNotification(user.getEmailNotification())
                                .userStatus(user.getUserStatus())
                                .rating(user.getRating())
                                .verifyEmail(user.getVerifyEmail() != null ? VerifyEmailVO.builder()
                                        .id(user.getVerifyEmail().getId())
                                        .user(UserVO.builder()
                                                .id(user.getVerifyEmail().getUser().getId())
                                                .name(user.getVerifyEmail().getUser().getName())
                                                .build())
                                        .expiryDate(user.getVerifyEmail().getExpiryDate())
                                        .token(user.getVerifyEmail().getToken())
                                        .build() : null)
                                .refreshTokenKey(user.getRefreshTokenKey())
                                .ownSecurity(user.getOwnSecurity() != null ? OwnSecurityVO.builder()
                                        .id(user.getOwnSecurity().getId())
                                        .password(user.getOwnSecurity().getPassword())
                                        .user(UserVO.builder()
                                                .id(user.getOwnSecurity().getUser().getId())
                                                .email(user.getOwnSecurity().getUser().getEmail())
                                                .build())
                                        .build() : null)
                                .dateOfRegistration(user.getDateOfRegistration())
                                .profilePicturePath(user.getProfilePicturePath())
                                .city(user.getCity())
                                .showShoppingList(user.getShowShoppingList())
                                .showEcoPlace(user.getShowEcoPlace())
                                .showLocation(user.getShowLocation())
                                .build())
                        .collect(Collectors.toSet()))
                .currentUserLiked(habit.isCurrentUserLiked())
                .userId(habit.getUserId())
                .isCustomHabit(habit.getIsCustomHabit())
                .build();
    }

}
