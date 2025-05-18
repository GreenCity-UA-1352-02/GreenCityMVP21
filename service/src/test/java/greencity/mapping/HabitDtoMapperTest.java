package greencity.mapping;

import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.ShoppingListItemStatus;
import greencity.enums.TagType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HabitDtoMapperTest {

    @InjectMocks
    private HabitDtoMapper mapper;

    @Test
    void convert_FromHabitTranslationToHabitDto_FullData() {

        Language ukrainian = Language.builder().id(1L).code("uk").build();
        Language english = Language.builder().id(2L).code("en").build();

        Habit habit = Habit.builder()
            .id(10L)
            .image("habit_image.jpg")
            .defaultDuration(21)
            .complexity(3)
            .tags(Set.of(
                Tag.builder().id(100L).tagTranslations(List.of(
                    TagTranslation.builder().id(101L).name("Здоров'я").language(ukrainian).build(),
                    TagTranslation.builder().id(102L).name("Health").language(english).build())).type(TagType.HABIT)
                    .build(),
                Tag.builder().id(110L).tagTranslations(List.of(
                    TagTranslation.builder().id(111L).name("Спорт").language(ukrainian).build(),
                    TagTranslation.builder().id(112L).name("Sport").language(english).build())).type(TagType.HABIT)
                    .build()))
            .shoppingListItems(Set.of(
                ShoppingListItem.builder().id(20L).translations(List.of(
                    ShoppingListItemTranslation.builder().id(21L).content("Купити яблука").language(ukrainian).build(),
                    ShoppingListItemTranslation.builder().id(22L).content("Buy apples").language(english).build()))
                    .build(),
                ShoppingListItem.builder().id(30L).translations(List.of(
                    ShoppingListItemTranslation.builder().id(31L).content("Випити води").language(ukrainian).build(),
                    ShoppingListItemTranslation.builder().id(32L).content("Drink water").language(english).build()))
                    .build()))
            .build();

        HabitTranslation habitTranslationUk = HabitTranslation.builder()
            .id(5L)
            .name("Ранкова зарядка")
            .description("Комплекс вправ для бадьорості")
            .habitItem("Робити зарядку")
            .language(ukrainian)
            .habit(habit)
            .build();

        HabitDto habitDto = mapper.convert(habitTranslationUk);

        assertNotNull(habitDto);
        assertEquals(habit.getId(), habitDto.getId());
        assertEquals(habit.getImage(), habitDto.getImage());
        assertEquals(habit.getDefaultDuration(), habitDto.getDefaultDuration());
        assertEquals(habit.getComplexity(), habitDto.getComplexity());

        assertNotNull(habitDto.getHabitTranslation());
        assertEquals(habitTranslationUk.getName(), habitDto.getHabitTranslation().getName());
        assertEquals(habitTranslationUk.getDescription(), habitDto.getHabitTranslation().getDescription());
        assertEquals(habitTranslationUk.getHabitItem(), habitDto.getHabitTranslation().getHabitItem());
        assertEquals(ukrainian.getCode(), habitDto.getHabitTranslation().getLanguageCode());

        assertNotNull(habitDto.getTags());
        assertEquals(2, habitDto.getTags().size());
        assertTrue(habitDto.getTags().containsAll(List.of("Здоров'я", "Спорт")));

        assertNotNull(habitDto.getShoppingListItems());
        assertEquals(2, habitDto.getShoppingListItems().size());

        Optional<ShoppingListItemDto> appleItem = habitDto.getShoppingListItems().stream()
            .filter(item -> "Купити яблука".equals(item.getText()))
            .findFirst();
        assertTrue(appleItem.isPresent());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), appleItem.get().getStatus());
        assertEquals(20L, appleItem.get().getId());

        Optional<ShoppingListItemDto> waterItem = habitDto.getShoppingListItems().stream()
            .filter(item -> "Випити води".equals(item.getText()))
            .findFirst();
        assertTrue(waterItem.isPresent());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), waterItem.get().getStatus());
        assertEquals(30L, waterItem.get().getId());
    }

    @Test
    void convert_FromHabitTranslationToHabitDto_NoShoppingListItems() {

        Language english = Language.builder().id(2L).code("en").build();
        Habit habit = Habit.builder()
            .id(15L)
            .image("another_image.png")
            .defaultDuration(14)
            .complexity(1)
            .tags(Set.of(
                Tag.builder().id(200L).tagTranslations(List.of(
                    TagTranslation.builder().id(201L).name("Easy").language(english).build())).type(TagType.HABIT)
                    .build()))
            .shoppingListItems(null)
            .build();

        HabitTranslation habitTranslationEn = HabitTranslation.builder()
            .id(8L)
            .name("Morning exercise")
            .description("A set of exercises to feel energetic")
            .habitItem("Do exercise")
            .language(english)
            .habit(habit)
            .build();

        HabitDto habitDto = mapper.convert(habitTranslationEn);

        assertNotNull(habitDto);
        assertEquals(habit.getId(), habitDto.getId());
        assertEquals(habit.getImage(), habitDto.getImage());
        assertEquals(habit.getDefaultDuration(), habitDto.getDefaultDuration());
        assertEquals(habit.getComplexity(), habitDto.getComplexity());

        assertNotNull(habitDto.getHabitTranslation());
        assertEquals(habitTranslationEn.getName(), habitDto.getHabitTranslation().getName());
        assertEquals(habitTranslationEn.getDescription(), habitDto.getHabitTranslation().getDescription());
        assertEquals(habitTranslationEn.getHabitItem(), habitDto.getHabitTranslation().getHabitItem());
        assertEquals(english.getCode(), habitDto.getHabitTranslation().getLanguageCode());

        assertNotNull(habitDto.getTags());
        assertEquals(1, habitDto.getTags().size());
        assertTrue(habitDto.getTags().contains("Easy"));

        assertNotNull(habitDto.getShoppingListItems());
        assertTrue(habitDto.getShoppingListItems().isEmpty());
    }

    @Test
    void convert_FromHabitTranslationToHabitDto_NoTags() {

        Language ukrainian = Language.builder().id(1L).code("uk").build();
        Habit habit = Habit.builder()
            .id(20L)
            .image("no_tags.jpg")
            .defaultDuration(7)
            .complexity(5)
            .tags(Set.of())
            .shoppingListItems(Set.of(
                ShoppingListItem.builder().id(40L).translations(List.of(
                    ShoppingListItemTranslation.builder().id(41L).content("Прибрати кімнату").language(ukrainian)
                        .build()))
                    .build()))
            .build();

        HabitTranslation habitTranslationUk = HabitTranslation.builder()
            .id(12L)
            .name("Прибирання")
            .description("Регулярне прибирання оселі")
            .habitItem("Прибирати")
            .language(ukrainian)
            .habit(habit)
            .build();

        HabitDto habitDto = mapper.convert(habitTranslationUk);

        assertNotNull(habitDto);
        assertEquals(habit.getId(), habitDto.getId());
        assertEquals(habit.getImage(), habitDto.getImage());
        assertEquals(habit.getDefaultDuration(), habitDto.getDefaultDuration());
        assertEquals(habit.getComplexity(), habitDto.getComplexity());

        assertNotNull(habitDto.getHabitTranslation());
        assertEquals(habitTranslationUk.getName(), habitDto.getHabitTranslation().getName());
        assertEquals(habitTranslationUk.getDescription(), habitDto.getHabitTranslation().getDescription());
        assertEquals(habitTranslationUk.getHabitItem(), habitDto.getHabitTranslation().getHabitItem());
        assertEquals(ukrainian.getCode(), habitDto.getHabitTranslation().getLanguageCode());

        assertNotNull(habitDto.getTags());
        assertTrue(habitDto.getTags().isEmpty());

        assertNotNull(habitDto.getShoppingListItems());
        assertEquals(1, habitDto.getShoppingListItems().size());
        assertEquals("Прибрати кімнату", habitDto.getShoppingListItems().getFirst().getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), habitDto.getShoppingListItems().getFirst().getStatus());
    }

    @Test
    void convert_FromHabitTranslationToHabitDto_ShoppingListItemWithoutTranslationForCurrentLanguage() {

        Language ukrainian = Language.builder().id(1L).code("uk").build();
        Language english = Language.builder().id(2L).code("en").build();
        Habit habit = Habit.builder()
            .id(25L)
            .image("no_translation.jpg")
            .defaultDuration(5)
            .complexity(2)
            .tags(Set.of())
            .shoppingListItems(Set.of(
                ShoppingListItem.builder().id(50L).translations(List.of(
                    ShoppingListItemTranslation.builder().id(51L).content("Buy milk").language(english).build()))
                    .build()))
            .build();

        HabitTranslation habitTranslationUk = HabitTranslation.builder()
            .id(15L)
            .name("Пити молоко")
            .description("Регулярне вживання молока")
            .habitItem("Випивати склянку молока")
            .language(ukrainian)
            .habit(habit)
            .build();

        HabitDto habitDto = mapper.convert(habitTranslationUk);

        assertNotNull(habitDto);
        assertEquals(habit.getId(), habitDto.getId());
        assertEquals(habit.getImage(), habitDto.getImage());
        assertEquals(habit.getDefaultDuration(), habitDto.getDefaultDuration());
        assertEquals(habit.getComplexity(), habitDto.getComplexity());

        assertNotNull(habitDto.getHabitTranslation());
        assertEquals(habitTranslationUk.getName(), habitDto.getHabitTranslation().getName());
        assertEquals(habitTranslationUk.getDescription(), habitDto.getHabitTranslation().getDescription());
        assertEquals(habitTranslationUk.getHabitItem(), habitDto.getHabitTranslation().getHabitItem());
        assertEquals(ukrainian.getCode(), habitDto.getHabitTranslation().getLanguageCode());

        assertNotNull(habitDto.getTags());
        assertTrue(habitDto.getTags().isEmpty());

        assertNotNull(habitDto.getShoppingListItems());
        assertEquals(1, habitDto.getShoppingListItems().size());
        assertNull(habitDto.getShoppingListItems().getFirst().getText());
        assertEquals(50L, habitDto.getShoppingListItems().getFirst().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), habitDto.getShoppingListItems().getFirst().getStatus());
    }
}
