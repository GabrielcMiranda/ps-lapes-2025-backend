package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.CreateReviewRequest;
import lapes.cesupa.ps_backend.dto.ReviewResponse;
import lapes.cesupa.ps_backend.dto.UpdateReviewRequest;
import lapes.cesupa.ps_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController{

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_COSTUMER')")
    public ResponseEntity<Void> createReview(@RequestBody CreateReviewRequest dto, @AuthenticationPrincipal Jwt jwt) {
        reviewService.create(dto, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>>listReviews() {
        var reviews = reviewService.list();
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_COSTUMER')")
    public ResponseEntity<Void> updateReview(@PathVariable Long id, @RequestBody UpdateReviewRequest dto, @AuthenticationPrincipal Jwt jwt) {
        reviewService.update(id, dto, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
     public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
