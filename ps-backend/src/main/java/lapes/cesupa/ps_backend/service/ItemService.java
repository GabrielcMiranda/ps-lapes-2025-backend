package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Locale.Category;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lapes.cesupa.ps_backend.dto.AddItemPhoto;
import lapes.cesupa.ps_backend.dto.CreateItem;
import lapes.cesupa.ps_backend.dto.GetItemResponse;
import lapes.cesupa.ps_backend.dto.ListItemResponse;
import lapes.cesupa.ps_backend.model.Item;
import lapes.cesupa.ps_backend.model.ItemImage;
import lapes.cesupa.ps_backend.repository.ItemRepository;
import lapes.cesupa.ps_backend.specification.ItemSpecifications;

@Service
public class ItemService {

    private final CategoryService categoryService;

    private final ItemRepository itemRepository;
    private final String uploadItemsDir;
    private final ImageService imageService;

    public ItemService(ItemRepository itemRepository, @Value("${app.upload.items-path}") String uploadItemsDir, CategoryService categoryService, ImageService imageService) {
        this.itemRepository = itemRepository;
        this.uploadItemsDir = uploadItemsDir;
        this.categoryService = categoryService;
        this.imageService = imageService;
    }

    @Transactional
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
            .and(ItemSpecifications.isAvailable())
            .and(ItemSpecifications.hasCategory(categoryId))
            .and(ItemSpecifications.priceGreaterThanOrEqual(minPrice))
            .and(ItemSpecifications.priceLessThanOrEqual(maxPrice))
            .and(ItemSpecifications.nameOrDescriptionContains(search));

         return itemRepository.findAll(spec, pageable)
                .map(i -> new ListItemResponse(
                    i.getId(),
                    i.getName(),
                    i.getDescription(),
                    i.getPriceInCents(),
                    i.getEstimatedPreptime(),
                    imageService.listItemImageUrls(i),
                    i.getExtraAttributes()
                ));
    }

    public GetItemResponse get(Long id){
        var item = validateItemId(id);
        List<String> imageUrls = imageService.listItemImageUrls(item);

        return new GetItemResponse(item.getName(),item.getDescription(),imageUrls,item.isAvailable());
    }

    @Transactional
    public Item update(Long id,CreateItem dto){
        var item = validateItemId(id);

        if(dto.name()!=null && !dto.name().isBlank()){
            item.setName(dto.name());
        }

        if(dto.description()!=null && !dto.description().isBlank()){
            item.setDescription(dto.description());
        }

        if (dto.priceInCents() != null && dto.priceInCents() > 0) {
            item.setPriceInCents(dto.priceInCents());
        }

        if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
            var categories = categoryService.findAllById(dto.categoryIds());
            item.setCategories(categories);
        }

        if (dto.estimatedPrepTime() != null && dto.estimatedPrepTime() > 0) {
            item.setEstimatedPreptime(dto.estimatedPrepTime());
        }

        if (dto.isAvailable() != null) {
            item.setAvailable(dto.isAvailable());
        }

        if(dto.extraAttributes()!=null && !dto.extraAttributes().isBlank()){
            item.setExtraAttributes(dto.extraAttributes());
        }

        item.setUpdatedAt(LocalDateTime.now());

        return itemRepository.save(item);
    }

    @Transactional
    public Item addPhoto(Long id, AddItemPhoto dto){
        var item = validateItemId(id);

        if (dto.files() == null || dto.files().length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No files uploaded");
        
        }
        for(MultipartFile file: dto.files()){
            imageService.validateImageType(file.getContentType());
        }

        List<ItemImage> existingImages = item.getImages();
        int total = existingImages.size() + dto.files().length;
        if (total > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "max 5 photos allowed per item");
        }

        int startPosition = existingImages.size() + 1;

        for (MultipartFile file : dto.files()) {
            String path = imageService.storeImage(uploadItemsDir, file);

            ItemImage image = new ItemImage();
            image.setUrl(path);
            image.setPosition(startPosition++);
            image.setItem(item);  

            item.getImages().add(image);
        }
        return itemRepository.save(item);
    }

    @Transactional
    public Item disableItem(Long id){
        var item = validateItemId(id);
        item.setAvailable(false);
        return itemRepository.save(item);
    }
    @Transactional
    public Item deletePhoto(Long id, Long photoId){
        var item = validateItemId(id);

        var images = item.getImages();

        ItemImage imageToRemove = images.stream()
            .filter(img -> img.getId().equals(photoId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found for this item"));

        images.remove(imageToRemove);

        int pos = 1;
        for (ItemImage img : images.stream().sorted(Comparator.comparing(ItemImage::getPosition)).toList()) {
            img.setPosition(pos++);
        }

        return itemRepository.save(item);
    }

    private void postItemValidation(String name){
        var item = itemRepository.findByName(name);

        if(item.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "this item already exists");
        }
    }

    private Item validateItemId(Long id){
        var item = itemRepository.findById(id);
        if(item.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
        }

        return item.get();
    }
}
