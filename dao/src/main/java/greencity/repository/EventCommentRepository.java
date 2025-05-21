package greencity.repository;

import greencity.entity.event.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    boolean existsByIdAndParentCommentIsNull(Long id);
}