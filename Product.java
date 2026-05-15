

/**
 * Abstract base class representing a product in the smart vending machine.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class Product {
    private int id;
    private String name;
    private double price; // in PKR
    private int quantity; // stock
    private String category;
    private String emoji;
    private String description;

    public Product(int id, String name, double price, int quantity, String category, String emoji, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.emoji = emoji;
        this.description = description;
    }

    public abstract String getProductType();
    public abstract String getNutritionalInfo();

    public boolean isAvailable() {
        return quantity > 0;
    }

    public void reduceStock(int qty) {
        if (quantity >= qty) {
            quantity -= qty;
        } else {
            throw new IllegalArgumentException("Not enough stock available.");
        }
    }

    public void restock(int qty) {
        if (qty > 0) {
            quantity += qty;
        }
    }

    // Getters and Setters with Encapsulation
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return emoji + " " + name + " (Rs. " + price + ") - " + getProductType();
    }
}
