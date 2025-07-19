package lapes.cesupa.ps_backend.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import lapes.cesupa.ps_backend.model.Order;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.model.Order.OrderStatus;

public class OrderSpecifications {

    public static Specification<Order> hasReceiver(User user) {
        return (root, query, cb) -> cb.equal(root.get("receiver"), user);
    }

      public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) -> startDate == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    public static Specification<Order> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) -> endDate == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }
}
