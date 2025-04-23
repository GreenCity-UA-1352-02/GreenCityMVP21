package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class HabitControllerTest {

    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private HabitController habitController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        HabitController habitController = new HabitController(habitService, tagsService);
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders.standaloneSetup(habitController)
                .setCustomArgumentResolvers(pageableResolver)
                .build();
    }

    @Test
    public void getHabitById_Success() throws Exception {
        Long id = 1L;
        Locale locale = Locale.ENGLISH;
        HabitDto habitDto = HabitDto.builder()
                .id(id)
                .defaultDuration(21)
                .build();

        when(habitService.getByIdAndLanguageCode(id, locale.getLanguage())).thenReturn(habitDto);

        mockMvc.perform(get("/habit/{id}", id)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.defaultDuration").value(21));

        verify(habitService, times(1)).getByIdAndLanguageCode(id, locale.getLanguage());

    }

    @Test
    public void getHabitById_InvalidHabitId_BadRequest() throws Exception {

        mockMvc.perform(get("/habit/invalid-id")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, Locale.ENGLISH.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(habitService, times(0)).getByIdAndLanguageCode(anyLong(), anyString());
    }

    @Test
    public void getAll_Success() throws Exception {
        Locale locale = Locale.ENGLISH;
        Pageable pageable = PageRequest.of(0, 10);

        List<HabitDto> habits = Arrays.asList(
                HabitDto.builder()
                        .id(1L)
                        .habitTranslation(HabitTranslationDto.builder().name("Habit 1").build())
                        .defaultDuration(21)
                        .habitAssignStatus(HabitAssignStatus.INPROGRESS)
                        .build(),
                HabitDto.builder()
                        .id(2L).habitTranslation(HabitTranslationDto.builder().name("Habit 2").build())
                        .defaultDuration(14)
                        .habitAssignStatus(HabitAssignStatus.ACQUIRED)
                        .build()
        );

        PageableDto<HabitDto> dto = new PageableDto<>(habits, habits.size(), 1, 1);

        when(habitService.getAllHabitsByLanguageCode(any(UserVO.class), eq(pageable), eq(locale.getLanguage())))
                .thenReturn(dto);

        mockMvc.perform(get("/habit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.page").isArray())
                .andExpect(jsonPath("$.page[0].id").value(1))
                .andExpect(jsonPath("$.page[0].habitTranslation.name").value("Habit 1"))
                .andExpect(jsonPath("$.page[1].id").value(2))
                .andExpect(jsonPath("$.page[1].habitTranslation.name").value("Habit 2"));

        verify(habitService, times(1))
                .getAllHabitsByLanguageCode(any(UserVO.class), eq(pageable), eq(locale.getLanguage()));
    }

    @Test
    public void getShoppingListItems_Success() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        List<ShoppingListItemDto> items = Arrays.asList(
                ShoppingListItemDto.builder()
                        .id(1L)
                        .text("Item 1")
                        .build(),
                ShoppingListItemDto.builder()
                        .id(2L)
                        .text("Item 2")
                        .build()
        );

        when(habitService.getShoppingListForHabit(habitId, locale.getLanguage()))
                .thenReturn(items);

        mockMvc.perform(get("/habit/{id}/shopping-list", habitId)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("Item 2"));

        verify(habitService, times(1)).getShoppingListForHabit(habitId, locale.getLanguage());

    }

    @Test
    public void getShoppingListItems_BadRequest() throws Exception {
        Locale locale = Locale.ENGLISH;

        mockMvc.perform(get("/habit/{id}/shopping-list", "invalidId")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(habitService, times(0)).getShoppingListForHabit(anyLong(), anyString());
    }

    @Test
    public void getAllByTagsAndLanguageCode_Success() throws Exception {
        Locale locale = Locale.ENGLISH;
        Pageable pageable = PageRequest.of(0, 10);
        List<String> tags = Arrays.asList("eco", "health");

        List<HabitDto> habits = Arrays.asList(
                HabitDto.builder()
                        .id(1L).habitTranslation(HabitTranslationDto.builder().name("Habit Eco").build())
                        .defaultDuration(10)
                        .build(),
                HabitDto.builder()
                        .id(2L).habitTranslation(HabitTranslationDto.builder().name("Habit Health").build())
                        .defaultDuration(14)
                        .build()
        );

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habits, habits.size(), 0, 1);

        when(habitService.getAllByTagsAndLanguageCode(eq(pageable), eq(tags), eq(locale.getLanguage())))
                .thenReturn(pageableDto);

        mockMvc.perform(get("/habit/tags/search")
                        .param("tags", "eco", "health")
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.page").isArray())
                .andExpect(jsonPath("$.page[0].id").value(1))
                .andExpect(jsonPath("$.page[0].habitTranslation.name").value("Habit Eco"))
                .andExpect(jsonPath("$.page[1].id").value(2))
                .andExpect(jsonPath("$.page[1].habitTranslation.name").value("Habit Health"));

        verify(habitService, times(1))
                .getAllByTagsAndLanguageCode(eq(pageable), eq(tags), eq(locale.getLanguage()));
    }

    @Test
    public void getAllByTagsAndLanguageCode_WithoutTags_BadRequest() throws Exception {
        mockMvc.perform(get("/habit/tags/search")
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, Locale.ENGLISH.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(habitService, times(0)).getAllByTagsAndLanguageCode(any(), any(), any());
    }

    @Test
    public void getAllByDifferentParameters_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Locale locale = Locale.ENGLISH;

        List<HabitDto> habits = List.of(
                HabitDto.builder()
                        .id(1L)
                        .habitTranslation(HabitTranslationDto.builder().name("Habit 1").build())
                        .defaultDuration(21)
                        .habitAssignStatus(HabitAssignStatus.INPROGRESS)
                        .build()
        );

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habits, 1, 1, 1);

        when(habitService.getAllByDifferentParameters(
                any(UserVO.class),
                eq(pageable),
                eq(Optional.of(List.of("eco"))),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(locale.getLanguage())))
                .thenReturn(pageableDto);

        mockMvc.perform(get("/habit/search")
                        .param("tags", "eco")
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.page[0].id").value(1))
                .andExpect(jsonPath("$.page[0].habitTranslation.name").value("Habit 1"));

        verify(habitService, times(1))
                .getAllByDifferentParameters(
                        any(UserVO.class),
                        eq(pageable),
                        eq(Optional.of(List.of("eco"))),
                        eq(Optional.empty()),
                        eq(Optional.empty()),
                        eq(locale.getLanguage())
                );
    }

    @Test
    public void getAllByDifferentParameters_NoParametersProvided_BadRequest() throws Exception {
        UserVO userVO = new UserVO();
        Locale locale = Locale.ENGLISH;
        Optional<List<String>> tags = Optional.empty();
        Optional<Boolean> isCustomHabit = Optional.empty();
        Optional<List<Integer>> complexities = Optional.empty();
        Pageable pageable = mock(Pageable.class);

        assertThrows(BadRequestException.class, () -> {
            habitController.getAllByDifferentParameters(userVO, locale, tags, isCustomHabit, complexities, pageable);
        }, "You should enter at least one parameter");

        verify(habitService, never()).getAllByDifferentParameters(userVO, pageable, tags,
                isCustomHabit, complexities, locale.getLanguage());
    }

    @Test
    public void findAllHabitsTags_Success() throws Exception {
        Locale locale = Locale.ENGLISH;
        List<String> tags = Arrays.asList("eco", "health", "fitness");

        when(tagsService.findAllHabitsTags(locale.getLanguage())).thenReturn(tags);

        mockMvc.perform(get("/habit/tags")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("eco"))
                .andExpect(jsonPath("$[1]").value("health"))
                .andExpect(jsonPath("$[2]").value("fitness"));

        verify(tagsService, times(1)).findAllHabitsTags(eq(locale.getLanguage()));
    }

    @Test
    public void addCustomHabit_Created_201() throws Exception {

        AddCustomHabitDtoRequest request = AddCustomHabitDtoRequest.builder()
                .complexity(1)
                .defaultDuration(21)
                .habitTranslations(Collections.singletonList(
                        HabitTranslationDto.builder()
                                .languageCode("en")
                                .name("Test Habit")
                                .description("Test Description")
                                .build()
                ))
                .image("test-image.jpg")
                .customShoppingListItemDto(Collections.singletonList(
                        CustomShoppingListItemResponseDto.builder()
                                .id(1L)
                                .text("Test Item")
                                .build()
                ))
                .tagIds(new HashSet<>(Collections.singletonList(1L)))
                .build();

        MultipartFile image = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        AddCustomHabitDtoResponse expectedResponse = AddCustomHabitDtoResponse.builder()
                .id(1L)
                .userId(123L)
                .image("test-image.jpg")
                .complexity(1)
                .defaultDuration(21)
                .customShoppingListItemDto(Collections.singletonList(
                        CustomShoppingListItemResponseDto.builder()
                                .id(1L)
                                .text("Test Item")
                                .build()
                ))
                .habitTranslations(Collections.singletonList(
                        HabitTranslationDto.builder()
                                .languageCode("en")
                                .name("Test Habit")
                                .description("Test Description")
                                .build()
                ))
                .tagIds(new HashSet<>(Collections.singletonList(1L)))
                .build();

        when(habitService.addCustomHabit(any(AddCustomHabitDtoRequest.class), eq(image), eq("testUser")))
                .thenReturn(expectedResponse);

        ResponseEntity<AddCustomHabitDtoResponse> response = habitController.addCustomHabit(request, image, principal);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(expectedResponse, response.getBody());

        verify(habitService, times(1)).addCustomHabit(eq(request), eq(image), eq("testUser"));
    }

    @Test
    public void addCustomHabit_InvalidParamsProvided_BadRequest() throws Exception {
        mockMvc.perform(multipart("/habit/custom")
                        .param("complexity", "")
                        .param("tagIds", "")
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isBadRequest());

        verify(habitService, times(0)).addCustomHabit(any(AddCustomHabitDtoRequest.class), any(MultipartFile.class), anyString());
    }

    @Test
    public void getFriendsAssignedToHabitProfilePictures_Success() throws Exception {
        Long habitId = 1L;

        List<UserProfilePictureDto> profilePictures = Arrays.asList(
                UserProfilePictureDto.builder().id(456L).name("Friend One").profilePicturePath("friend1_pic.jpg").build(),
                UserProfilePictureDto.builder().id(789L).name("Friend Two").profilePicturePath("friend2_pic.png").build()
        );

        when(habitService.getFriendsAssignedToHabitProfilePictures(habitId, null))
                .thenReturn(profilePictures);

        mockMvc.perform(MockMvcRequestBuilders.get("/habit/{habitId}/friends/profile-pictures", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(456))
                .andExpect(jsonPath("$[0].name").value("Friend One"))
                .andExpect(jsonPath("$[0].profilePicturePath").value("friend1_pic.jpg"))
                .andExpect(jsonPath("$[1].id").value(789))
                .andExpect(jsonPath("$[1].name").value("Friend Two"))
                .andExpect(jsonPath("$[1].profilePicturePath").value("friend2_pic.png"));

        verify(habitService, times(1)).getFriendsAssignedToHabitProfilePictures(eq(habitId), isNull());
    }
}