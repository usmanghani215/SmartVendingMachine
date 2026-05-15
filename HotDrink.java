

/**
 * HotDrink subclass representing hot beverages.
 */
public class HotDrink extends Product {
    public HotDrink(int id, String name, double price, int quantity, String emoji, String description) {
        super(id, name, price, quantity, "Hot Drinks", emoji, description);
    }

    @Override
    public String getProductType() {
        return "Hot Drink";
    }

    @Override
    public String getNutritionalInfo() {
        return "Warm and comforting. Contains caffeine in some variants.";
    }
}
