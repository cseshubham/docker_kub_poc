package com.sp.inventory_ms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateInventory(@RequestBody InventoryUpdateRequest request) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(request.getProductId());

        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            if (inventory.getAvailableQuantity() >= request.getQuantity()) {
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());
                inventoryRepository.save(inventory);
                return ResponseEntity.ok("Inventory updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found in inventory.");
        }
    }

    // DTO for the update request
    static class InventoryUpdateRequest {
        private Long productId;
        private int quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}