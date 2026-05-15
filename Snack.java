

/**
 * Snack subclass representing snack items.
 */
public class Snack extends Product {
    public Snack(int id, String name, double price, int quantity, String emoji, String description) {
        super(id, name, price, quantity, "Snacks", emoji, description);
    }

    @Override
    public String getProductType() {
        return "Snack";
    }

    @Override
    public String getNutritionalInfo() {
        return "High in carbs and energy. Enjoy in moderation!";
    }
}
