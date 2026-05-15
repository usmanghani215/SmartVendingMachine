

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shopping cart logic managing the items a user intends to purchase.
 */
public class Cart {
    private LinkedHashMap<Product, Integer> items;

    public Cart() {
        this.items = new LinkedHashMap<>();
    }

    public void addItem(Product p, int qty) {
        if (items.containsKey(p)) {
            int currentQty = items.get(p);
            if (currentQty + qty <= p.getQuantity()) {
                items.put(p, currentQty + qty);
            } else {
                throw new IllegalArgumentException("Cannot add more than available stock.");
            }
        } else {
            if (qty <= p.getQuantity()) {
                items.put(p, qty);
            } else {
                throw new IllegalArgumentException("Cannot add more than available stock.");
            }
        }
    }

    public void removeItem(Product p) {
        items.remove(p);
    }

    public void updateQuantity(Product p, int qty) {
        if (qty <= 0) {
            removeItem(p);
        } else if (qty <= p.getQuantity()) {
            items.put(p, qty);
        } else {
            throw new IllegalArgumentException("Requested quantity exceeds available stock.");
        }
    }

    public void clearCart() {
        items.clear();
    }

    public double getSubtotal() {
        double subtotal = 0.0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            subtotal += (entry.getKey().getPrice() * entry.getValue());
        }
        return subtotal;
    }

    public double getTax() {
        return getSubtotal() * 0.05; // 5% tax
    }

    public double getTotal() {
        return getSubtotal() + getTax();
    }

    public int getTotalItems() {
        int count = 0;
        for (int qty : items.values()) {
            count += qty;
        }
        return count;
    }

    public String getCartSummary() {
        return "Items: " + getTotalItems() + " | Total: Rs. " + String.format("%.2f", getTotal());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public LinkedHashMap<Product, Integer> getItems() {
        return items;
    }
}
