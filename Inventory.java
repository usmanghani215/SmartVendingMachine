

import java.util.ArrayList;

/**
 * Manages the collection of products available in the vending machine.
 */
public class Inventory {
    private ArrayList<Product> products;

    public Inventory() {
        this.products = new ArrayList<>();
        initializeDefaultProducts();
    }

    private void initializeDefaultProducts() {
        // SNACKS
        addProduct(new Snack(101, "Sooper Biscuit", 30.0, 15, "🍪", "Classic Pakistani biscuit."));
        addProduct(new Snack(102, "Bisconni Chocolatto", 40.0, 12, "🍫", "Chocolate filled biscuit."));
        addProduct(new Snack(103, "Peek Freans Rio", 35.0, 10, "🥮", "Cream biscuit."));
        addProduct(new Snack(104, "Nimco Mix", 60.0, 8, "🥜", "Spicy and crunchy mix."));
        addProduct(new Snack(105, "Local Potato Chips", 50.0, 20, "🥔", "Crispy salted potato chips."));
        addProduct(new Snack(106, "Zeera Biscuit", 25.0, 18, "🫙", "Cumin flavored biscuit."));

        // BEVERAGES
        addProduct(new Beverage(201, "Pakola Drink", 70.0, 15, "🥤", "Ice cream soda."));
        addProduct(new Beverage(202, "Shezan Mango Juice", 55.0, 12, "🥭", "Refreshing mango juice."));
        addProduct(new Beverage(203, "Mineral Water (500ml)", 40.0, 25, "💧", "Pure drinking water."));
        addProduct(new Beverage(204, "Rooh Afza Drink", 65.0, 10, "🌹", "Rose flavored syrup drink."));
        addProduct(new Beverage(205, "Gourmet Cola", 60.0, 14, "🫙", "Local cola drink."));
        addProduct(new Beverage(206, "Lassi Pack", 75.0, 8, "🥛", "Traditional yogurt drink."));

        // HEALTHY ITEMS
        addProduct(new HealthyItem(301, "Dates Pack (200g)", 120.0, 10, "🌴", "Premium dates."));
        addProduct(new HealthyItem(302, "Mixed Dry Fruits", 180.0, 6, "🥜", "Assorted nuts and raisins."));
        addProduct(new HealthyItem(303, "Chana Chaat Cup", 80.0, 9, "🫘", "Spicy chickpea snack."));
        addProduct(new HealthyItem(304, "Fruit Salad Cup", 100.0, 7, "🍱", "Fresh cut fruits."));
        addProduct(new HealthyItem(305, "Honey Sachet", 90.0, 12, "🍯", "Pure natural honey."));
        addProduct(new HealthyItem(306, "Granola Bar", 110.0, 8, "🌾", "Oats and honey bar."));

        // HOT DRINKS
        addProduct(new HotDrink(401, "Chai (Tea)", 50.0, 30, "☕", "Traditional Pakistani tea."));
        addProduct(new HotDrink(402, "Doodh Patti", 60.0, 25, "🍵", "Milk-based strong tea."));
        addProduct(new HotDrink(403, "Nescafé Instant Coffee", 80.0, 20, "☕", "Instant hot coffee."));
        addProduct(new HotDrink(404, "Hot Chocolate", 100.0, 15, "🍫", "Rich hot cocoa."));
        addProduct(new HotDrink(405, "Green Tea", 70.0, 18, "🍵", "Healthy green tea leaves."));
        addProduct(new HotDrink(406, "Kashmiri Chai", 90.0, 12, "🌸", "Pink tea with crushed nuts."));
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void removeProduct(int id) {
        products.removeIf(p -> p.getId() == id);
    }

    public Product findById(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Product findByName(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Product> getByCategory(String category) {
        ArrayList<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory().equalsIgnoreCase(category) || p.getProductType().equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public ArrayList<Product> getLowStockItems() {
        ArrayList<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getQuantity() < 3) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }

    public ArrayList<Product> getAvailableProducts() {
        ArrayList<Product> available = new ArrayList<>();
        for (Product p : products) {
            if (p.isAvailable()) {
                available.add(p);
            }
        }
        return available;
    }

    public void restockProduct(int id, int qty) {
        Product p = findById(id);
        if (p != null) {
            p.restock(qty);
        }
    }

    public ArrayList<Product> searchProducts(String keyword) {
        ArrayList<Product> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(lowerKeyword) || 
                p.getCategory().toLowerCase().contains(lowerKeyword) || 
                p.getDescription().toLowerCase().contains(lowerKeyword)) {
                results.add(p);
            }
        }
        return results;
    }

    public int getTotalProductCount() {
        return products.size();
    }

    public double getTotalInventoryValue() {
        double total = 0.0;
        for (Product p : products) {
            total += (p.getPrice() * p.getQuantity());
        }
        return total;
    }

    public String getInventorySummary() {
        return "Total Products: " + getTotalProductCount() + " | Inventory Value: Rs. " + getTotalInventoryValue();
    }

    public ArrayList<Product> getAllProducts() {
        return products;
    }
}
