package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.dto.CreateItem;
import lapes.cesupa.ps_backend.dto.ListItemResponse;
import lapes.cesupa.ps_backend.model.Item;
import lapes.cesupa.ps_backend.repository.ItemRepository;
import lapes.cesupa.ps_backend.specification.ItemSpecifications;

@Service
public class ItemService {

    private final CategoryService categoryService;

    private final ItemRepository itemRepository;
    private final String uploadItemsDir;

    public ItemService(ItemRepository itemRepository, @Value("${app.upload.items-path}") String uploadItemsDir, CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.uploadItemsDir = uploadItemsDir;
        this.categoryService = categoryService;
    }

    public Item create(CreateItem dto){
        postItemValidation(dto.name());

        var now = LocalDateTime.now();
        var categories = categoryService.findAllById(dto.categoryIds());

        var item = new Item();
        item.setName(dto.name());
        item.setDescription(dto.description());
        item.setPriceInCents(dto.priceInCents());
        item.setCategories(categories);
        item.setEstimatedPreptime(dto.estimatedPrepTime());
        item.setAvailable(dto.isAvailable());
        item.setExtraAttributes(dto.extraAttributes());
        item.setCreatedAt(now);
        item.setUpdatedAt(now);

        return itemRepository.save(item);
    }

    public Page<ListItemResponse> listAll(Long categoryId, Integer minPrice, Integer maxPrice, String search, Pageable pageable){
       Specification<Item> spec = Specification.<Item>where(null)
            .and(ItemSpecifications.hasCategory(categoryId))
            .and(ItemSpecifications.priceGreaterThanOrEqual(minPrice))
            .and(ItemSpecifications.priceLessThanOrEqual(maxPrice))
            .and(ItemSpecifications.nameOrDescriptionContains(search));

         return itemRepository.findAll(spec, pageable)
                .map(d -> new ListItemResponse(
                    d.getId(),
                    d.getName(),
                    d.getDescription(),
                    d.getPriceInCents(),
                    d.isAvailable()
                ));
        }

    private void postItemValidation(String name){
        var item = itemRepository.findByName(name);

        if(item.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "this item already exists");
        }
    }
}
