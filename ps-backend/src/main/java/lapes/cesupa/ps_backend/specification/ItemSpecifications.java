package lapes.cesupa.ps_backend.specification;

import org.springframework.data.jpa.domain.Specification;

import lapes.cesupa.ps_backend.model.Item;

public class ItemSpecifications {

    public static Specification<Item> hasCategory(Long categoryId) {
        return (root, query, builder) -> 
            categoryId == null ? null : 
            builder.equal(root.join("categories").get("id"), categoryId);
    }
    
    public static Specification<Item> priceGreaterThanOrEqual(Integer minPrice) {
        return (root, query, builder) ->
            minPrice == null ? null : builder.greaterThanOrEqualTo(root.get("priceInCents"), minPrice);
    }

    public static Specification<Item> priceLessThanOrEqual(Integer maxPrice) {
        return (root, query, builder) ->
            maxPrice == null ? null : builder.lessThanOrEqualTo(root.get("priceInCents"), maxPrice);
    }

    public static Specification<Item> nameOrDescriptionContains(String search) {
        return (root, query, builder) ->
            (search == null || search.isBlank()) ? null :
            builder.or(
                builder.like(builder.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                builder.like(builder.lower(root.get("description")), "%" + search.toLowerCase() + "%")
            );
    }
}
