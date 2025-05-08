package greencity.entity;


import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;


public class UserSpecification implements Specification<User> {
    private final Long currentUserId;
    private final String searchTerm;
    private final Boolean filterByCity;
    private final Boolean filterByMutualFriends;
    private final String city;
    private final Long friendId;

    public UserSpecification(Long currentUserId, String searchTerm, Boolean filterByCity, Boolean filterByMutualFriends,
                             String city, Long friendId) {
        this.currentUserId = currentUserId;
        this.searchTerm = searchTerm;
        this.filterByCity = filterByCity;
        this.filterByMutualFriends = filterByMutualFriends;
        this.city = city;
        this.friendId = friendId;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.distinct(true);
        List<Predicate> predicates = new ArrayList<>();

        // Поиск по имени
        if (searchTerm != null && !searchTerm.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchTerm.toLowerCase() + "%"));
        }

        // Исключение текущего пользователя
        predicates.add(cb.notEqual(root.get("id"), currentUserId));

        // Фильтрация по городу
        if (filterByCity != null && filterByCity) {
            Subquery<String> citySubquery = query.subquery(String.class);
            Root<User> currentUser = citySubquery.from(User.class);
            citySubquery.select(currentUser.get("city"));
            citySubquery.where(cb.equal(currentUser.get("id"), currentUserId));

            predicates.add(cb.equal(cb.lower(root.get("city")), cb.lower(citySubquery)));
        } else if (city != null && !city.isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
        }

        if (filterByMutualFriends != null && filterByMutualFriends) {
            // Подзапрос: получить ID всех друзей текущего пользователя
            Subquery<Long> myFriendsSubquery = query.subquery(Long.class);
            Root<Friend> myFriendRoot = myFriendsSubquery.from(Friend.class);
            myFriendsSubquery.select(myFriendRoot.get("friend").get("id"))
                .where(cb.equal(myFriendRoot.get("user").get("id"), currentUserId));

            // Подзапрос: получить ID друзей моих друзей
            Subquery<Long> friendsOfFriendsSubquery = query.subquery(Long.class);
            Root<Friend> fofRoot = friendsOfFriendsSubquery.from(Friend.class);
            friendsOfFriendsSubquery.select(fofRoot.get("friend").get("id"))
                .where(fofRoot.get("user").get("id").in(myFriendsSubquery));

            // Главный предикат: root.id входит либо в друзей, либо в друзей друзей
            predicates.add(cb.or(
                root.get("id").in(myFriendsSubquery),
                root.get("id").in(friendsOfFriendsSubquery)
            ));
        }

        // Фильтрация по FriendId (если нужно)
        if (friendId != null) {
            predicates.add(cb.equal(root.get("id"), friendId));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
