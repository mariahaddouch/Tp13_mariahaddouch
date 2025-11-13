package ma.rest.springmvc.controller;

import ma.rest.entity.Category;
import ma.rest.entity.Item;
import ma.rest.springmvc.repository.CategoryRepository;
import ma.rest.springmvc.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Value("${app.fetch.mode:LAZY}")
    private String fetchMode;

    public CategoryController(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Page<Category> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.created(URI.create("/categories/" + savedCategory.getId()))
                .body(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDetails.getName());
                    existingCategory.setCode(categoryDetails.getCode());
                    existingCategory.setUpdatedAt(java.time.Instant.now());
                    Category updatedCategory = categoryRepository.save(existingCategory);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    categoryRepository.delete(category);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().<Void>build());
    }

    @GetMapping("/{id}/items")
    public Page<Item> getItemsForCategory(@PathVariable Long id, Pageable pageable) {
        if ("JOIN".equalsIgnoreCase(fetchMode)) {
            return itemRepository.findByCategoryIdWithFetch(id, pageable);
        }
        return itemRepository.findByCategoryId(id, pageable);
    }
}