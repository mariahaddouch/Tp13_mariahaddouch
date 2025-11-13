package ma.rest.springmvc.controller;

import ma.rest.entity.Item;
import ma.rest.springmvc.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;

    @Value("${app.fetch.mode:LAZY}")
    private String fetchMode;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Page<Item> getItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @GetMapping(params = "categoryId")
    public Page<Item> getItemsByCategory(@RequestParam Long categoryId, Pageable pageable) {
        if ("JOIN".equalsIgnoreCase(fetchMode)) {
            return itemRepository.findByCategoryIdWithFetch(categoryId, pageable);
        }
        return itemRepository.findByCategoryId(categoryId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item savedItem = itemRepository.save(item);
        return ResponseEntity.created(URI.create("/items/" + savedItem.getId()))
                .body(savedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @Valid @RequestBody Item itemDetails) {
        return itemRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setName(itemDetails.getName());
                    existingItem.setSku(itemDetails.getSku());
                    existingItem.setPrice(itemDetails.getPrice());
                    existingItem.setStock(itemDetails.getStock());
                    existingItem.setCategory(itemDetails.getCategory());
                    existingItem.setUpdatedAt(java.time.Instant.now());
                    Item updatedItem = itemRepository.save(existingItem);
                    return ResponseEntity.ok(updatedItem);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(item -> {
                    itemRepository.delete(item);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().<Void>build());
    }
}