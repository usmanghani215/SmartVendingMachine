

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles payment and transaction records.
 */
public class Transaction {
    private String transactionId;
    private LocalDateTime timestamp;
    private LinkedHashMap<Product, Integer> purchasedItems;
    private double totalAmount;
    private String paymentMethod;
    private boolean isSuccessful;

    public static ArrayList<Transaction> history = new ArrayList<>();

    public Transaction(LinkedHashMap<Product, Integer> purchasedItems, double totalAmount, String paymentMethod, boolean isSuccessful) {
        // Auto-generate transaction ID e.g., TXN-20240001
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.timestamp = LocalDateTime.now();
        this.purchasedItems = new LinkedHashMap<>(purchasedItems);
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.isSuccessful = isSuccessful;
    }

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        receipt.append("====================================\n");
        receipt.append("       SMART VENDING MACHINE        \n");
        receipt.append("====================================\n");
        receipt.append("Transaction ID: ").append(transactionId).append("\n");
        receipt.append("Date & Time: ").append(timestamp.format(formatter)).append("\n");
        receipt.append("Payment Method: ").append(paymentMethod).append("\n");
        receipt.append("Status: ").append(isSuccessful ? "SUCCESSFUL" : "FAILED").append("\n");
        receipt.append("------------------------------------\n");
        
        double subtotal = 0;
        for (Map.Entry<Product, Integer> entry : purchasedItems.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            double itemTotal = p.getPrice() * qty;
            subtotal += itemTotal;
            receipt.append(String.format("%-20s x%d  Rs. %6.2f\n", p.getName(), qty, itemTotal));
        }
        
        receipt.append("------------------------------------\n");
        receipt.append(String.format("Subtotal:                  Rs. %6.2f\n", subtotal));
        double tax = subtotal * 0.05;
        receipt.append(String.format("Tax (5%%):                  Rs. %6.2f\n", tax));
        receipt.append("====================================\n");
        receipt.append(String.format("TOTAL:                     Rs. %6.2f\n", totalAmount));
        receipt.append("====================================\n");
        receipt.append("       Thank you for your purchase!  \n");
        
        return receipt.toString();
    }

    public static void addTransaction(Transaction t) {
        history.add(t);
    }

    public static String getTransactionHistory() {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : history) {
            sb.append(t.transactionId).append(" | ").append(t.totalAmount).append(" | ").append(t.isSuccessful).append("\n");
        }
        return sb.toString();
    }

    public double getTotal() {
        return totalAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public LinkedHashMap<Product, Integer> getPurchasedItems() {
        return purchasedItems;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
