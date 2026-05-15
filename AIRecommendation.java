

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * Handles AI suggestions using Groq API and fallback logic.
 */
public class AIRecommendation {
    // Please replace with your actual Groq API key for production use.
    static final String GROQ_API_KEY = "YOUR_GROQ_API_KEY_HERE";
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public static String getRecommendation(String userMood, Cart currentCart, ArrayList<Product> available) {
        StringBuilder cartContents = new StringBuilder();
        if (currentCart.isEmpty()) {
            cartContents.append("Empty");
        } else {
            currentCart.getItems().forEach((p, qty) -> cartContents.append(p.getName()).append(" (").append(qty).append("), "));
        }

        StringBuilder availableProducts = new StringBuilder();
        int count = 0;
        for (Product p : available) {
            if (count++ > 15) break; // Limit to 15 to keep prompt size reasonable
            availableProducts.append(p.getName()).append(", ");
        }

        String prompt = "You are a smart vending machine AI assistant. The user is feeling " + userMood + ".\n" +
                        "Their current cart has: " + cartContents + ".\n" +
                        "Available products are: " + availableProducts + ".\n" +
                        "Suggest 2-3 products with brief reasons. Keep response under 100 words. Be friendly.";

        // Construct JSON manually
        String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
        String jsonPayload = "{\n" +
                             "  \"model\": \"llama3-8b-8192\",\n" +
                             "  \"messages\": [\n" +
                             "    {\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}\n" +
                             "  ]\n" +
                             "}";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + GROQ_API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Manual JSON parsing to extract "content" inside "message"
                String targetKey = "\"content\":\"";
                int startIndex = responseBody.indexOf(targetKey);
                if (startIndex != -1) {
                    startIndex += targetKey.length();
                    int endIndex = responseBody.indexOf("\"", startIndex);
                    // Handle edge cases where output might have escaped quotes
                    while (endIndex != -1 && responseBody.charAt(endIndex - 1) == '\\') {
                        endIndex = responseBody.indexOf("\"", endIndex + 1);
                    }
                    if (endIndex != -1) {
                        String content = responseBody.substring(startIndex, endIndex);
                        // unescape newlines
                        content = content.replace("\\n", "\n").replace("\\\"", "\"");
                        return content;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Groq API call failed, falling back to local recommendation. " + e.getMessage());
        }

        // Fallback local recommendation
        return getMoodBasedSuggestion(userMood);
    }

    public static String getMoodBasedSuggestion(String mood) {
        switch (mood.replaceAll("[^a-zA-Z-]", "").toLowerCase()) {
            case "hungry":
                return "You look hungry! I suggest grabbing some local potato chips or a heavy snack like Nimco Mix.";
            case "thirsty":
                return "Stay hydrated! A chilled Pakola Drink or some cold Mineral Water will refresh you.";
            case "tired":
                return "Need a boost? A hot cup of Chai or Nescafé Instant Coffee will wake you right up.";
            case "health-conscious":
                return "Great choice! Try our Fresh Fruit Salad Cup or Dates Pack for a healthy energy boost.";
            case "stressed":
                return "Take a deep breath. Some Hot Chocolate or a comforting Doodh Patti might help soothe your nerves.";
            default:
                return "How about trying something new today? Bisconni Chocolatto is a crowd favorite!";
        }
    }

    public static ArrayList<Product> getTopRecommended(Inventory inv, String mood) {
        ArrayList<Product> recommended = new ArrayList<>();
        ArrayList<Product> available = inv.getAvailableProducts();
        
        String targetCategory = "";
        switch (mood.replaceAll("[^a-zA-Z-]", "").toLowerCase()) {
            case "hungry": targetCategory = "Snacks"; break;
            case "thirsty": targetCategory = "Beverages"; break;
            case "tired": targetCategory = "Hot Drinks"; break;
            case "health-conscious": targetCategory = "Healthy"; break;
            case "stressed": targetCategory = "Hot Drinks"; break;
            default: targetCategory = "Snacks"; break;
        }

        for (Product p : available) {
            if (p.getCategory().equalsIgnoreCase(targetCategory) || targetCategory.isEmpty()) {
                recommended.add(p);
                if (recommended.size() >= 3) break;
            }
        }

        // If we still don't have 3, just add whatever is available
        for (Product p : available) {
            if (recommended.size() >= 3) break;
            if (!recommended.contains(p)) {
                recommended.add(p);
            }
        }

        return recommended;
    }

    public static String generateHealthTip(String category) {
        if (category.equalsIgnoreCase("Healthy")) {
            return "Tip: Nuts and dates provide long-lasting natural energy!";
        } else if (category.equalsIgnoreCase("Beverages")) {
            return "Tip: Aim to drink 8 glasses of water daily!";
        } else if (category.equalsIgnoreCase("Snacks")) {
            return "Tip: Enjoy snacks in moderation as part of a balanced diet.";
        } else if (category.equalsIgnoreCase("Hot Drinks")) {
            return "Tip: Green tea contains antioxidants that are great for your body.";
        }
        return "Tip: Always chew your food well!";
    }
}
