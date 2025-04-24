package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.HabitAssign;
import greencity.entity.UserShoppingListItem;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HabitAssignMapperTest {

    @InjectMocks
    private HabitAssignMapper mapper;

    @Test
    void convert_FromHabitAssignDtoToHabitAssign_FullDataWithShoppingListItems() {

        ZonedDateTime createDateTime = ZonedDateTime.of(LocalDateTime.now().minusDays(3), ZoneId.of("Europe/Kyiv"));
        ZonedDateTime lastEnrollmentDate = ZonedDateTime.of(LocalDateTime.now().minusDays(1), ZoneId.of("Europe/Kyiv"));

        UserShoppingListItemAdvanceDto item1 = UserShoppingListItemAdvanceDto.builder()
            .id(10L)
            .dateCompleted(LocalDateTime.now().minusDays(2))
            .status(ShoppingListItemStatus.INPROGRESS)
            .shoppingListItemId(100L)
            .build();
        UserShoppingListItemAdvanceDto item2 = UserShoppingListItemAdvanceDto.builder()
            .id(11L)
            .dateCompleted(LocalDateTime.now())
            .status(ShoppingListItemStatus.INPROGRESS)
            .shoppingListItemId(101L)
            .build();

        HabitDto habitDto = HabitDto.builder().id(5L).complexity(2).build();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
            .id(15L)
            .duration(30)
            .habitStreak(7)
            .createDateTime(createDateTime)
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(15)
            .lastEnrollmentDate(lastEnrollmentDate)
            .habit(habitDto)
            .userShoppingListItems(List.of(item1, item2))
            .build();

        HabitAssign habitAssign = mapper.convert(habitAssignDto);

        assertNotNull(habitAssign);
        assertEquals(habitAssignDto.getId(), habitAssign.getId());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getDuration());
        assertEquals(habitAssignDto.getHabitStreak(), habitAssign.getHabitStreak());
        assertEquals(habitAssignDto.getCreateDateTime(), habitAssign.getCreateDate());
        assertEquals(habitAssignDto.getStatus(), habitAssign.getStatus());
        assertEquals(habitAssignDto.getWorkingDays(), habitAssign.getWorkingDays());
        assertEquals(habitAssignDto.getLastEnrollmentDate(), habitAssign.getLastEnrollmentDate());

        assertNotNull(habitAssign.getHabit());
        assertEquals(habitAssignDto.getHabit().getId(), habitAssign.getHabit().getId());
        assertEquals(habitAssignDto.getHabit().getComplexity(), habitAssign.getHabit().getComplexity());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getHabit().getDefaultDuration());

        assertNotNull(habitAssign.getUserShoppingListItems());
        assertEquals(2, habitAssign.getUserShoppingListItems().size());

        UserShoppingListItem mappedItem = habitAssign.getUserShoppingListItems().getFirst();
        assertEquals(item1.getId(), mappedItem.getId());
        assertEquals(item1.getDateCompleted(), mappedItem.getDateCompleted());
        assertEquals(item1.getStatus(), mappedItem.getStatus());
        assertEquals(item1.getShoppingListItemId(), mappedItem.getShoppingListItem().getId());

        UserShoppingListItem mappedItem2 = habitAssign.getUserShoppingListItems().get(1);
        assertEquals(item2.getId(), mappedItem2.getId());
        assertEquals(item2.getDateCompleted(), mappedItem2.getDateCompleted());
        assertEquals(item2.getStatus(), mappedItem2.getStatus());
        assertEquals(item2.getShoppingListItemId(), mappedItem2.getShoppingListItem().getId());
    }

    @Test
    void convert_FromHabitAssignDtoToHabitAssign_OnlyInProgressShoppingListItemsAreAdded() {

        ZonedDateTime createDateTime = ZonedDateTime.of(LocalDateTime.now().minusDays(4), ZoneId.of("Europe/Kyiv"));
        HabitDto habitDto = HabitDto.builder().id(100L).complexity(3).build();

        UserShoppingListItemAdvanceDto itemInProgress1 = UserShoppingListItemAdvanceDto.builder()
            .id(20L)
            .dateCompleted(LocalDateTime.now().minusDays(3))
            .status(ShoppingListItemStatus.INPROGRESS)
            .shoppingListItemId(300L)
            .build();
        UserShoppingListItemAdvanceDto itemDone = UserShoppingListItemAdvanceDto.builder()
            .id(21L)
            .dateCompleted(LocalDateTime.now().minusDays(1))
            .status(ShoppingListItemStatus.DONE)
            .shoppingListItemId(301L)
            .build();
        UserShoppingListItemAdvanceDto itemPlanned = UserShoppingListItemAdvanceDto.builder()
            .id(22L)
            .dateCompleted(null)
            .status(ShoppingListItemStatus.DISABLED)
            .shoppingListItemId(302L)
            .build();
        UserShoppingListItemAdvanceDto itemInProgress2 = UserShoppingListItemAdvanceDto.builder()
            .id(23L)
            .dateCompleted(LocalDateTime.now())
            .status(ShoppingListItemStatus.INPROGRESS)
            .shoppingListItemId(303L)
            .build();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
            .id(40L)
            .duration(60)
            .habitStreak(15)
            .createDateTime(createDateTime)
            .status(HabitAssignStatus.ACTIVE)
            .workingDays(30)
            .lastEnrollmentDate(createDateTime)
            .habit(habitDto)
            .userShoppingListItems(List.of(itemInProgress1, itemDone, itemPlanned, itemInProgress2))
            .build();

        HabitAssign habitAssign = mapper.convert(habitAssignDto);

        assertNotNull(habitAssign);
        assertNotNull(habitAssign.getUserShoppingListItems());
        assertEquals(2, habitAssign.getUserShoppingListItems().size());

        List<ShoppingListItemStatus> actualStatuses = habitAssign.getUserShoppingListItems().stream()
            .map(UserShoppingListItem::getStatus)
            .toList();
        assertTrue(actualStatuses.contains(ShoppingListItemStatus.INPROGRESS));
        assertEquals(2, actualStatuses.stream().filter(ShoppingListItemStatus.INPROGRESS::equals).count());

        List<Long> actualItemIds = habitAssign.getUserShoppingListItems().stream()
            .map(UserShoppingListItem::getId)
            .toList();
        assertTrue(actualItemIds.contains(itemInProgress1.getId()));
        assertTrue(actualItemIds.contains(itemInProgress2.getId()));
        assertFalse(actualItemIds.contains(itemDone.getId()));
        assertFalse(actualItemIds.contains(itemPlanned.getId()));
    }

    @Test
    void convert_FromHabitAssignDtoToHabitAssign_MinimalDataWithoutShoppingListItems() {

        ZonedDateTime createDateTime = ZonedDateTime.now();
        HabitDto habitDto = HabitDto.builder().id(6L).complexity(1).build();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
            .id(20L)
            .duration(21)
            .habitStreak(0)
            .createDateTime(createDateTime)
            .status(HabitAssignStatus.ACTIVE)
            .workingDays(0)
            .lastEnrollmentDate(createDateTime)
            .habit(habitDto)
            .userShoppingListItems(List.of())
            .build();

        HabitAssign habitAssign = mapper.convert(habitAssignDto);

        assertNotNull(habitAssign);
        assertEquals(habitAssignDto.getId(), habitAssign.getId());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getDuration());
        assertEquals(habitAssignDto.getHabitStreak(), habitAssign.getHabitStreak());
        assertEquals(habitAssignDto.getCreateDateTime(), habitAssign.getCreateDate());
        assertEquals(habitAssignDto.getStatus(), habitAssign.getStatus());
        assertEquals(habitAssignDto.getWorkingDays(), habitAssign.getWorkingDays());
        assertEquals(habitAssignDto.getLastEnrollmentDate(), habitAssign.getLastEnrollmentDate());

        assertNotNull(habitAssign.getHabit());
        assertEquals(habitAssignDto.getHabit().getId(), habitAssign.getHabit().getId());
        assertEquals(habitAssignDto.getHabit().getComplexity(), habitAssign.getHabit().getComplexity());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getHabit().getDefaultDuration());

        assertNotNull(habitAssign.getUserShoppingListItems());
        assertTrue(habitAssign.getUserShoppingListItems().isEmpty());
    }

    @Test
    void convert_FromHabitAssignDtoToHabitAssign_NoShoppingListItems() {

        ZonedDateTime createDateTime = ZonedDateTime.now().minusDays(7);
        ZonedDateTime lastEnrollmentDate = ZonedDateTime.now().minusDays(2);
        HabitDto habitDto = HabitDto.builder().id(7L).complexity(3).build();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
            .id(25L)
            .duration(60)
            .habitStreak(10)
            .createDateTime(createDateTime)
            .status(HabitAssignStatus.ACQUIRED)
            .workingDays(30)
            .lastEnrollmentDate(lastEnrollmentDate)
            .habit(habitDto)
            .userShoppingListItems(Collections.emptyList())
            .build();

        HabitAssign habitAssign = mapper.convert(habitAssignDto);

        assertNotNull(habitAssign);
        assertEquals(habitAssignDto.getId(), habitAssign.getId());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getDuration());
        assertEquals(habitAssignDto.getHabitStreak(), habitAssign.getHabitStreak());
        assertEquals(habitAssignDto.getCreateDateTime(), habitAssign.getCreateDate());
        assertEquals(habitAssignDto.getStatus(), habitAssign.getStatus());
        assertEquals(habitAssignDto.getWorkingDays(), habitAssign.getWorkingDays());
        assertEquals(habitAssignDto.getLastEnrollmentDate(), habitAssign.getLastEnrollmentDate());

        assertNotNull(habitAssign.getHabit());
        assertEquals(habitAssignDto.getHabit().getId(), habitAssign.getHabit().getId());
        assertEquals(habitAssignDto.getHabit().getComplexity(), habitAssign.getHabit().getComplexity());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getHabit().getDefaultDuration());

        assertNotNull(habitAssign.getUserShoppingListItems());
        assertTrue(habitAssign.getUserShoppingListItems().isEmpty());
    }

    @Test
    void convert_FromHabitAssignDtoToHabitAssign_EmptyShoppingListItems() {

        ZonedDateTime createDateTime = ZonedDateTime.now().minusDays(1);
        ZonedDateTime lastEnrollmentDate = ZonedDateTime.now();
        HabitDto habitDto = HabitDto.builder().id(8L).complexity(4).build();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
            .id(30L)
            .duration(90)
            .habitStreak(20)
            .createDateTime(createDateTime)
            .status(HabitAssignStatus.ACTIVE)
            .workingDays(60)
            .lastEnrollmentDate(lastEnrollmentDate)
            .habit(habitDto)
            .userShoppingListItems(List.of())
            .build();

        HabitAssign habitAssign = mapper.convert(habitAssignDto);

        assertNotNull(habitAssign);
        assertEquals(habitAssignDto.getId(), habitAssign.getId());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getDuration());
        assertEquals(habitAssignDto.getHabitStreak(), habitAssign.getHabitStreak());
        assertEquals(habitAssignDto.getCreateDateTime(), habitAssign.getCreateDate());
        assertEquals(habitAssignDto.getStatus(), habitAssign.getStatus());
        assertEquals(habitAssignDto.getWorkingDays(), habitAssign.getWorkingDays());
        assertEquals(habitAssignDto.getLastEnrollmentDate(), habitAssign.getLastEnrollmentDate());

        assertNotNull(habitAssign.getHabit());
        assertEquals(habitAssignDto.getHabit().getId(), habitAssign.getHabit().getId());
        assertEquals(habitAssignDto.getHabit().getComplexity(), habitAssign.getHabit().getComplexity());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getHabit().getDefaultDuration());

        assertNotNull(habitAssign.getUserShoppingListItems());
        assertTrue(habitAssign.getUserShoppingListItems().isEmpty());
    }
}
