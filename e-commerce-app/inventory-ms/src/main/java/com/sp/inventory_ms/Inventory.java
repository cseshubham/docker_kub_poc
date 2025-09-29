package com.sp.inventory_ms;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Inventory {

    @Id
    private Long productId;
    private int availableQuantity;

    public Inventory() {
    }

    public Inventory(Long productId, int availableQuantity) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
