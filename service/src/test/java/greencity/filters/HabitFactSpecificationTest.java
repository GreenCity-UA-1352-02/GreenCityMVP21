package greencity.filters;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.*;
import greencity.entity.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitFactSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Root<HabitFact> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private Join<HabitFact, Habit> habitJoin;

    @Mock
    private Join<HabitFactTranslation, HabitFact> habitFactTranslationJoin;

    @Mock
    private Predicate predicate;

    private HabitFactSpecification habitFactSpecification;

    @BeforeEach
    public void setUp() {
        habitFactSpecification = new HabitFactSpecification(new ArrayList<>());
        criteriaBuilder = mock(CriteriaBuilder.class);
        criteriaQuery = mock(CriteriaQuery.class);
        root = mock(Root.class);
        predicate = mock(Predicate.class);
        habitJoin = mock(Join.class);
    }

    @Test
    public void testToPredicateFiltersById() {
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .type("id")
                .value(1L)
                .build();

        List<SearchCriteria> criteriaList = Collections.singletonList(searchCriteria);
        habitFactSpecification = new HabitFactSpecification(criteriaList);

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaBuilder.and(eq(predicate), any(Predicate.class))).thenReturn(predicate);
        when(criteriaBuilder.equal(root.get(HabitFact_.id), 1L)).thenReturn(predicate);

        Predicate result = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder, times(1)).equal(root.get(HabitFact_.id), 1L);
        verify(criteriaBuilder, times(1)).and(predicate, predicate);
    }

    @Test
    public void testToPredicateHandlesHabitIdWithJoin() {
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .type("habitId")
                .value(2L)
                .build();

        List<SearchCriteria> criteriaList = Collections.singletonList(searchCriteria);
        habitFactSpecification = new HabitFactSpecification(criteriaList);

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaBuilder.and(eq(predicate), any(Predicate.class))).thenReturn(predicate);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(criteriaBuilder.equal(habitJoin.get(Habit_.id), 2L)).thenReturn(predicate);

        Predicate result = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(root, times(1)).join(HabitFact_.habit);
        verify(criteriaBuilder, times(1)).equal(habitJoin.get(Habit_.id), 2L);
    }

    @Test
    public void testToPredicateFiltersByContent() {
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .type("content")
                .value("test content")
                .build();

        List<SearchCriteria> criteriaList = Collections.singletonList(searchCriteria);
        habitFactSpecification = new HabitFactSpecification(criteriaList);

        @SuppressWarnings("unchecked")
        Path<String> contentPath = (Path<String>) mock(Path.class);
        @SuppressWarnings("unchecked")
        Root<HabitFactTranslation> habitFactTranslationRoot = (Root<HabitFactTranslation>) mock(Root.class);

        when(habitFactTranslationRoot.get(anyString())).thenAnswer(invocation -> {
            String attribute = invocation.getArgument(0);
            if ("content".equals(attribute)) {
                return contentPath;
            } else {
                throw new IllegalArgumentException("Unexpected attribute: " + attribute);
            }
        });

        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        when(criteriaBuilder.like(contentPath, "%test content%")).thenReturn(predicate);

        Path<HabitFact> habitFactPath = mock(Path.class);
        Path<Long> habitFactIdPath = mock(Path.class);
        Path<Long> rootIdPath = mock(Path.class);

        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(habitFactPath);
        when(habitFactPath.get(HabitFact_.id)).thenReturn(habitFactIdPath);
        when(root.get(HabitFact_.id)).thenReturn(rootIdPath);
        when(criteriaBuilder.equal(habitFactIdPath, rootIdPath)).thenReturn(predicate);

        Predicate result = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaQuery, times(1)).from(HabitFactTranslation.class);
        verify(criteriaBuilder, times(1)).equal(habitFactIdPath, rootIdPath);
    }

    @Test
    public void testToPredicateHandlesEmptyContent() {
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .type("content")
                .value("")
                .build();

        List<SearchCriteria> criteriaList = Collections.singletonList(searchCriteria);
        habitFactSpecification = new HabitFactSpecification(criteriaList);

        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        Predicate result = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
    }

    @Test
    public void testToPredicateCombinesMultipleCriteria() {
        SearchCriteria searchCriteria1 = SearchCriteria.builder()
                .type("id")
                .value(1L)
                .build();
        SearchCriteria searchCriteria2 = SearchCriteria.builder()
                .type("habitId")
                .value(2L)
                .build();
        SearchCriteria searchCriteria3 = SearchCriteria.builder()
                .type("content")
                .value("test content")
                .build();

        List<SearchCriteria> criteriaList = Arrays.asList(searchCriteria1, searchCriteria2, searchCriteria3);
        habitFactSpecification = new HabitFactSpecification(criteriaList);

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaBuilder.and(eq(predicate), any(Predicate.class))).thenReturn(predicate);
        when(criteriaBuilder.equal(root.get(HabitFact_.id), 1L)).thenReturn(predicate);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(criteriaBuilder.equal(habitJoin.get(Habit_.id), 2L)).thenReturn(predicate);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        Path<String> contentPath = mock(Path.class);
        Path<HabitFact> habitFactPath = mock(Path.class);
        Path<Long> habitFactIdPath = mock(Path.class);
        Path<Long> rootIdPath = mock(Path.class);

        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);
        doReturn(contentPath).when(habitFactTranslationRoot).get("content");
        when(criteriaBuilder.like(contentPath, "%test content%")).thenReturn(predicate);

        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(habitFactPath);
        when(habitFactPath.get(HabitFact_.id)).thenReturn(habitFactIdPath);
        when(root.get(HabitFact_.id)).thenReturn(rootIdPath);
        when(criteriaBuilder.equal(habitFactIdPath, rootIdPath)).thenReturn(predicate);

        Predicate result = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        verify(criteriaBuilder, times(1)).equal(habitJoin.get(Habit_.id), 2L);
        verify(criteriaQuery, times(1)).from(HabitFactTranslation.class);
        verify(criteriaBuilder, times(1)).equal(habitFactIdPath, rootIdPath);
    }
}
