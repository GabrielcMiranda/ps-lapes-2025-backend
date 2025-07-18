package lapes.cesupa.ps_backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "categories")
public class Category {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "category_id")
        private Long id;

        @Column(unique = true)
        private String name;

        private String description;

        private String imageUrl;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        @ManyToMany(mappedBy = "categories")
        private Set<Item> items = new HashSet<>();
}
