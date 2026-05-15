

/**
 * Beverage subclass representing cold drinks.
 */
public class Beverage extends Product {
    public Beverage(int id, String name, double price, int quantity, String emoji, String description) {
        super(id, name, price, quantity, "Beverages", emoji, description);
    }

    @Override
    public String getProductType() {
        return "Beverage";
    }

    @Override
    public String getNutritionalInfo() {
        return "Refreshing cold drink. Best served chilled.";
    }
}
