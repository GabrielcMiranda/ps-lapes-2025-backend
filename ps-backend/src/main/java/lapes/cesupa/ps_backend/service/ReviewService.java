package lapes.cesupa.ps_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lapes.cesupa.ps_backend.dto.CreateReviewRequest;
import lapes.cesupa.ps_backend.dto.ReviewResponse;
import lapes.cesupa.ps_backend.model.Review;
import lapes.cesupa.ps_backend.model.Order.OrderStatus;
import lapes.cesupa.ps_backend.repository.ItemRepository;
import lapes.cesupa.ps_backend.repository.OrderRepository;
import lapes.cesupa.ps_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ItemRepository itemRepository;

    private final AuthService authService;

    private final OrderRepository orderRepository;

    @Transactional
    public Review create(CreateReviewRequest dto, String userId) {

        var customer = authService.validateUserId(userId);
        var item = itemRepository.findById(dto.itemId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        var hasOrderedItem = orderRepository.findAll().stream()
            .filter(order -> order.getReceiver().equals(customer))
            .filter(order -> order.getOrderStatus().equals(OrderStatus.DELIVERED))
            .flatMap(order -> order.getItems().stream())
            .anyMatch(i -> item.getId().equals(dto.itemId()));
        
        if (!hasOrderedItem) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only review items you have received in completed orders");
        }
        
        Review review = new Review();
        review.setItem(item);
        review.setCustomer(customer);
        review.setRating(dto.rating());
        review.setComment(dto.comment());

        return reviewRepository.save(review);
    }

    public List<ReviewResponse> list(){
        var reviews = reviewRepository.findAll().stream()
            .map(review -> new ReviewResponse(review.getCustomer().getUsername(),
            review.getItem().getName(),
            review.getRating(),
            review.getComment())).toList();

        if(reviews.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no reviews were found");
        }
        return reviews;
    }
}
