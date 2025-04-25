package greencity.repository;

import greencity.entity.NewsSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsSubscriberRepo extends JpaRepository<NewsSubscriber, Long> {

    /**
     * Method checks if email already exists in the database.
     *
     * @param email - email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
