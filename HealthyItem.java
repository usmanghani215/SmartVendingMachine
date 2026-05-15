

/**
 * HealthyItem subclass representing healthy snacks.
 */
public class HealthyItem extends Product {
    public HealthyItem(int id, String name, double price, int quantity, String emoji, String description) {
        super(id, name, price, quantity, "Healthy", emoji, description);
    }

    @Override
    public String getProductType() {
        return "Healthy";
    }

    @Override
    public String getNutritionalInfo() {
        return "Rich in vitamins and minerals. Great for a healthy lifestyle!";
    }
}
