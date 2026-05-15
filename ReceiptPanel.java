

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Dialog to show the receipt and provide an option to save it to a file.
 */
public class ReceiptPanel extends JDialog {
    private JTextArea receiptArea;
    private Transaction transaction;

    public ReceiptPanel(JFrame parent, Transaction transaction) {
        super(parent, "Transaction Receipt", true);
        this.transaction = transaction;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(ThemeManager.getBg());

        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        receiptArea.setText(transaction.generateReceipt());
        receiptArea.setBackground(ThemeManager.getCardBg());
        receiptArea.setForeground(ThemeManager.getText());
        receiptArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ThemeManager.getBg());

        JButton printBtn = createStyledButton("Print Receipt", ThemeManager.getPrimary());
        JButton doneBtn = createStyledButton("Done", ThemeManager.getSuccess());

        printBtn.addActionListener(e -> saveReceiptToFile());
        doneBtn.addActionListener(e -> dispose());

        buttonPanel.add(printBtn);
        buttonPanel.add(doneBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private JButton createStyledButton(String text, java.awt.Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));
        btn.setBackground(bg);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    private void saveReceiptToFile() {
        String filename = "receipt_" + transaction.getTransactionId() + ".txt";
        try (FileWriter writer = new FileWriter(filename, java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write(transaction.generateReceipt());
            JOptionPane.showMessageDialog(this, "Receipt saved as " + filename, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving receipt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
