package greencity.repository;

import greencity.entity.HabitComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitCommentRepo extends JpaRepository<HabitComment, Long> {
    /**
     * Method returns all {@link HabitComment} by page.
     *
     * @param pageable  page of news.
     * @param habitId of {@link greencity.entity.Habit} for which comments we
     *                  search.
     * @return all {@link HabitComment} by page.
     */
    Page<HabitComment> findAllByParentCommentIsNullAndHabitIdOrderByCreatedDateDesc(Pageable pageable,
                                                                                        Long habitId);

    /**
     * Method returns all replies to comment, specified by parentCommentId and by
     * page.
     *
     * @param pageable        page of news.
     * @param parentCommentId id of comment, replies to which we get.
     * @return all replies to comment, specified by parentCommentId and page.
     */
    Page<HabitComment> findAllByParentCommentIdOrderByCreatedDateDesc(Pageable pageable,
                                                                        Long parentCommentId);

    /**
     * Method returns count of replies to comment, specified by parentCommentId.
     *
     * @param parentCommentId id of comment, count of replies to which we get.
     * @return count of replies to comment, specified by parentCommentId.
     */
    @Query("SELECT count(hb) from HabitComment hb where hb.parentComment.id = ?1 AND hb.deleted = FALSE")
    int countByParentCommentId(Long parentCommentId);

    /**
     * The method returns the count of not deleted comments, specified by habitId.
     *
     * @return count of comments, specified by {@link greencity.entity.Habit}.
     */
    @Query("SELECT count(ec) FROM HabitComment ec "
            + "WHERE ec.parentComment IS NULL AND ec.habit.id = ?1 AND ec.deleted = FALSE")
    int countOfComments(Long habitId);

    /**
     * The method returns the count of not deleted comments, specified by
     * {@link greencity.entity.Habit}.
     *
     * @return count of comments, specified by {@link greencity.entity.Habit}
     */
    @Query(value = "SELECT count(ec.id) FROM habit_comment ec "
            + "JOIN habits en ON en.id = ec.habit_id "
            + "WHERE en.id = :habitId AND ec.deleted <> 'true'", nativeQuery = true)
    int countHabitCommentByHabit(Long habitId);

    /**
     * Method returns all {@link HabitComment} by page.
     *
     * @param pageable  page of news.
     * @param habitId id of {@link greencity.entity.Habit} for which comments we
     *                  search.
     * @return all active {@link HabitComment} by page.
     * @author Dovganyuk Taras
     */
    Page<HabitComment> findAllByParentCommentIsNullAndDeletedFalseAndHabitIdOrderByCreatedDateDesc(
            Pageable pageable,
            Long habitId);

    /**
     * Method returns all {@link HabitComment} by page.
     *
     * @param pageable        page of news.
     * @param parentCommentId id of comment, replies to which we get.
     * @return all replies to comment, specified by parentCommentId and page.
     * @author Dovganyuk Taras
     */
    Page<HabitComment> findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateDesc(Pageable pageable,
                                                                                       Long parentCommentId);
}
