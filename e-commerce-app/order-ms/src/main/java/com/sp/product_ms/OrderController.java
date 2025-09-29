package com.sp.product_ms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private OrderRepository orderRepository;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order orderRequest) {
        Long productId = orderRequest.getProductId();
        int quantity = orderRequest.getQuantity();

        try {
            // 1. Check if product exists (optional, but good practice)
            // We assume if inventory exists, product exists.

            // 2. Check inventory
            String inventoryUrl = inventoryServiceUrl + "/" + productId;
            ResponseEntity<InventoryStatus> inventoryResponse = restTemplate.getForEntity(inventoryUrl, InventoryStatus.class);

            if (inventoryResponse.getStatusCode() != HttpStatus.OK || inventoryResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found in inventory.");
            }

            if (inventoryResponse.getBody().getAvailableQuantity() < quantity) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock for product ID: " + productId);
            }

            // 3. Update inventory
            String updateInventoryUrl = inventoryServiceUrl + "/update";
            InventoryUpdateRequest updateRequest = new InventoryUpdateRequest(productId, quantity);
            restTemplate.postForEntity(updateInventoryUrl, updateRequest, Void.class);

            // 4. Create and save the order (in-memory for this example)
            Order newOrder = new Order(productId, quantity);
            newOrder.setStatus("PLACED");
            Order savedOrder = orderRepository.save(newOrder);


            System.out.println("Order Placed: " + savedOrder.getId() + " for product " + savedOrder.getProductId());

            return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully with ID: " + savedOrder.getId());

        } catch (Exception e) {
            // In a real app, you would have more sophisticated error handling and potentially a rollback mechanism.
            System.err.println("Error placing order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not place order. Please try again later.");
        }
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DTO classes for communication
    static class InventoryStatus {
        private int availableQuantity;

        public int getAvailableQuantity() {
            return availableQuantity;
        }

        public void setAvailableQuantity(int availableQuantity) {
            this.availableQuantity = availableQuantity;
        }
    }

    static class InventoryUpdateRequest {
        private Long productId;
        private int quantity;

        public InventoryUpdateRequest(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
