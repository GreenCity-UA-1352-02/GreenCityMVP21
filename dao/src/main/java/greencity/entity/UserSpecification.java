package greencity.entity;

import greencity.enums.FriendStatus;
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

        predicates.add(cb.notEqual(root.get("id"), currentUserId));

        if (searchTerm != null && !searchTerm.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchTerm.toLowerCase() + "%"));
        }

        if (filterByCity != null && filterByCity) {
            predicates.add(createCityEqualityPredicate(query, cb, root));
        } else if (city != null && !city.isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
        }

        if (filterByMutualFriends != null && filterByMutualFriends) {
            predicates.add(createFilterByMutualFriendsPredicate(query, cb, root));
        }

        if (friendId != null) {
            predicates.add(cb.equal(root.get("id"), friendId));
        }

        predicates.add(cb.not(root.get("id").in(createExistingFriendsSubquery(query, cb))));

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate createCityEqualityPredicate(CriteriaQuery<?> query, CriteriaBuilder cb, Root<User> root) {
        Subquery<String> citySubquery = query.subquery(String.class);
        Root<User> currentUser = citySubquery.from(User.class);
        citySubquery.select(currentUser.get("city"))
            .where(cb.equal(currentUser.get("id"), currentUserId));
        return cb.equal(cb.lower(root.get("city")), cb.lower(citySubquery));
    }

    private Predicate createFilterByMutualFriendsPredicate(CriteriaQuery<?> query, CriteriaBuilder cb,
                                                           Root<User> root) {
        Subquery<Long> myFriendsSubquery = createFriendsSubquery(query, cb, currentUserId);

        Subquery<Long> friendsOfFriendsSubquery = query.subquery(Long.class);
        Root<Friend> fofRoot = friendsOfFriendsSubquery.from(Friend.class);
        friendsOfFriendsSubquery.select(fofRoot.get("friend").get("id"))
            .where(cb.and(
                fofRoot.get("user").get("id").in(myFriendsSubquery),
                cb.equal(fofRoot.get("status"), FriendStatus.FRIEND)
            ));

        return root.get("id").in(friendsOfFriendsSubquery);
    }

    private Subquery<Long> createExistingFriendsSubquery(CriteriaQuery<?> query, CriteriaBuilder cb) {
        return createFriendsSubquery(query, cb, currentUserId);
    }

    private Subquery<Long> createFriendsSubquery(CriteriaQuery<?> query, CriteriaBuilder cb, Long userId) {
        Subquery<Long> friendsSubquery = query.subquery(Long.class);
        Root<Friend> friendRoot = friendsSubquery.from(Friend.class);
        friendsSubquery.select(friendRoot.get("friend").get("id"))
            .where(cb.equal(friendRoot.get("user").get("id"), userId),
                cb.equal(friendRoot.get("status"), FriendStatus.FRIEND));
        return friendsSubquery;
    }
}
