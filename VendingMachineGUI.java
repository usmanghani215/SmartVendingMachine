

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class VendingMachineGUI extends JFrame {
    private Inventory inventory;
    private Cart cart;
    private String currentCategory = "All";
    
    // UI Components
    private JPanel centerGridPanel;
    private JPanel cartItemsPanel;
    private JLabel subtotalLabel, taxLabel, totalLabel, cartCountLabel;
    private JComboBox<String> paymentCombo;
    private JLabel statusLabel;
    private JTextField searchField;
    private JTabbedPane categoryTabs;

    public VendingMachineGUI() {
        inventory = new Inventory();
        cart = new Cart();

        setTitle("🤖 Smart Vending Machine — NUML University");
        setSize(1280, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getBg());

        setupHeader();
        setupCenterGrid();
        setupCartPanel();
        setupFooter();
        
        // Keyboard shortcut for theme toggle
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "toggleTheme");
        am.put("toggleTheme", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });

        refreshProductGrid(inventory.getAllProducts());
    }

    private void setupHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(ThemeManager.getHeaderBg());
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("🤖 Smart Vending Machine");
        titleLabel.setFont(ThemeManager.getTitleFont().deriveFont(Font.BOLD, 28f));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        searchField = new JTextField(15);
        searchField.setFont(ThemeManager.getBodyFont());
        searchField.putClientProperty("JTextField.placeholderText", "Search products...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String keyword = searchField.getText();
                if (keyword.isEmpty()) {
                    filterByCategory(currentCategory);
                } else {
                    refreshProductGrid(inventory.searchProducts(keyword));
                }
            }
        });

        JButton themeBtn = new JButton("🌙 Toggle Theme");
        styleHeaderButton(themeBtn);
        themeBtn.addActionListener(e -> toggleTheme());

        rightHeader.add(searchField);
        rightHeader.add(themeBtn);

        categoryTabs = new JTabbedPane();
        categoryTabs.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));
        categoryTabs.addTab("All", null);
        categoryTabs.addTab("Snacks", null);
        categoryTabs.addTab("Beverages", null);
        categoryTabs.addTab("Healthy", null);
        categoryTabs.addTab("Hot Drinks", null);
        categoryTabs.addChangeListener(e -> {
            currentCategory = categoryTabs.getTitleAt(categoryTabs.getSelectedIndex());
            filterByCategory(currentCategory);
        });

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(titleLabel, BorderLayout.WEST);
        topContainer.add(rightHeader, BorderLayout.EAST);

        headerPanel.add(topContainer, BorderLayout.NORTH);
        headerPanel.add(categoryTabs, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void filterByCategory(String category) {
        searchField.setText("");
        if (category.equals("All")) {
            refreshProductGrid(inventory.getAllProducts());
        } else {
            refreshProductGrid(inventory.getByCategory(category));
        }
    }

    private void styleHeaderButton(JButton btn) {
        btn.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));
        btn.setBackground(ThemeManager.getAccent());
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupCenterGrid() {
        centerGridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        centerGridPanel.setBackground(ThemeManager.getBg());
        
        JScrollPane scrollPane = new JScrollPane(centerGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ThemeManager.getBg());
        scrollPane.getViewport().setBackground(ThemeManager.getBg());

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshProductGrid(ArrayList<Product> products) {
        centerGridPanel.removeAll();
        for (Product p : products) {
            ProductCard card = new ProductCard(p, this);
            centerGridPanel.add(card);
        }
        centerGridPanel.revalidate();
        centerGridPanel.repaint();
    }

    private void setupCartPanel() {
        JPanel eastPanel = new JPanel(new BorderLayout(0, 10));
        eastPanel.setPreferredSize(new Dimension(350, 0));
        eastPanel.setBackground(ThemeManager.getCardBg());
        eastPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, ThemeManager.getBorder()),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Cart Header
        JPanel cartHeader = new JPanel(new BorderLayout());
        cartHeader.setOpaque(false);
        JLabel cartTitle = new JLabel("🛒 Your Cart");
        cartTitle.setFont(ThemeManager.getTitleFont());
        cartTitle.setForeground(ThemeManager.getText());
        
        cartCountLabel = new JLabel("0 items");
        cartCountLabel.setFont(ThemeManager.getBodyFont());
        cartCountLabel.setForeground(ThemeManager.getPrimary());
        
        cartHeader.add(cartTitle, BorderLayout.WEST);
        cartHeader.add(cartCountLabel, BorderLayout.EAST);

        // Cart Items List
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(ThemeManager.getCardBg());
        JScrollPane cartScroll = new JScrollPane(cartItemsPanel);
        cartScroll.setBorder(null);
        cartScroll.setBackground(ThemeManager.getCardBg());

        // Cart Totals & Checkout
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setOpaque(false);

        subtotalLabel = createTotalLabel("Subtotal: Rs. 0.00");
        taxLabel = createTotalLabel("Tax (5%): Rs. 0.00");
        totalLabel = createTotalLabel("TOTAL: Rs. 0.00");
        totalLabel.setFont(ThemeManager.getTitleFont());
        totalLabel.setForeground(ThemeManager.getPrimary());

        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        paymentPanel.setOpaque(false);
        JLabel payLabel = new JLabel("Payment: ");
        payLabel.setFont(ThemeManager.getBodyFont());
        payLabel.setForeground(ThemeManager.getText());
        paymentCombo = new JComboBox<>(new String[]{"Cash", "Card", "EasyPaisa"});
        paymentCombo.setFont(ThemeManager.getBodyFont());
        paymentPanel.add(payLabel);
        paymentPanel.add(paymentCombo);

        JButton suggestBtn = createStyledButton("🤖 AI Suggest", ThemeManager.getAccent());
        JButton checkoutBtn = createStyledButton("✅ CHECKOUT", ThemeManager.getSuccess());
        JButton clearBtn = createStyledButton("🗑️ Clear Cart", ThemeManager.getDanger());

        suggestBtn.addActionListener(e -> showAISuggestionDialog());
        checkoutBtn.addActionListener(e -> processCheckout());
        clearBtn.addActionListener(e -> { cart.clearCart(); updateCartUI(); });

        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(checkoutBtn);
        btnPanel.add(suggestBtn);
        btnPanel.add(clearBtn);

        totalsPanel.add(new JSeparator());
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(subtotalLabel);
        totalsPanel.add(taxLabel);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        totalsPanel.add(totalLabel);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(paymentPanel);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        totalsPanel.add(btnPanel);

        eastPanel.add(cartHeader, BorderLayout.NORTH);
        eastPanel.add(cartScroll, BorderLayout.CENTER);
        eastPanel.add(totalsPanel, BorderLayout.SOUTH);

        add(eastPanel, BorderLayout.EAST);
    }

    private JLabel createTotalLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeManager.getBodyFont());
        lbl.setForeground(ThemeManager.getText());
        lbl.setAlignmentX(RIGHT_ALIGNMENT);
        return lbl;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void addToCart(Product p) {
        try {
            cart.addItem(p, 1);
            updateCartUI();
            setStatus("Added " + p.getName() + " to cart.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Out of Stock", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateCartUI() {
        cartItemsPanel.removeAll();
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();

            JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
            itemPanel.setOpaque(false);
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorder()),
                    new EmptyBorder(10, 5, 10, 5)
            ));

            JLabel nameLbl = new JLabel(p.getEmoji() + " " + p.getName());
            nameLbl.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));
            nameLbl.setForeground(ThemeManager.getText());
            
            JLabel priceLbl = new JLabel(String.format("Rs. %.0f", p.getPrice() * qty));
            priceLbl.setFont(ThemeManager.getBodyFont());
            priceLbl.setForeground(ThemeManager.getAccent());

            JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            qtyPanel.setOpaque(false);
            JButton minusBtn = new JButton("-");
            JButton plusBtn = new JButton("+");
            JLabel qtyLbl = new JLabel(String.valueOf(qty));
            qtyLbl.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));
            qtyLbl.setForeground(ThemeManager.getText());

            styleQtyButton(minusBtn);
            styleQtyButton(plusBtn);

            minusBtn.addActionListener(e -> {
                cart.updateQuantity(p, qty - 1);
                updateCartUI();
            });
            plusBtn.addActionListener(e -> {
                try {
                    cart.updateQuantity(p, qty + 1);
                    updateCartUI();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            });

            qtyPanel.add(minusBtn);
            qtyPanel.add(qtyLbl);
            qtyPanel.add(plusBtn);

            JPanel topPnl = new JPanel(new BorderLayout());
            topPnl.setOpaque(false);
            topPnl.add(nameLbl, BorderLayout.WEST);
            topPnl.add(priceLbl, BorderLayout.EAST);

            itemPanel.add(topPnl, BorderLayout.NORTH);
            itemPanel.add(qtyPanel, BorderLayout.EAST);

            cartItemsPanel.add(itemPanel);
        }

        subtotalLabel.setText(String.format("Subtotal: Rs. %.2f", cart.getSubtotal()));
        taxLabel.setText(String.format("Tax (5%%): Rs. %.2f", cart.getTax()));
        totalLabel.setText(String.format("TOTAL: Rs. %.2f", cart.getTotal()));
        cartCountLabel.setText(cart.getTotalItems() + " items");

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private void styleQtyButton(JButton btn) {
        btn.setPreferredSize(new Dimension(25, 25));
        btn.setMargin(new Insets(0,0,0,0));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(ThemeManager.getBorder());
        btn.setForeground(ThemeManager.getText());
        btn.setFocusPainted(false);
    }

    private void setupFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(ThemeManager.getCardBg());
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel leftFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftFooter.setOpaque(false);

        JButton invBtn = createStyledButton("📦 Inventory", ThemeManager.getPrimary());
        JButton txnBtn = createStyledButton("🧾 Transactions", ThemeManager.getPrimary());
        JButton statsBtn = createStyledButton("📊 Stats", ThemeManager.getPrimary());

        invBtn.addActionListener(e -> showAdminInventory());
        txnBtn.addActionListener(e -> showTransactionHistory());
        statsBtn.addActionListener(e -> showStats());

        leftFooter.add(invBtn);
        leftFooter.add(txnBtn);
        leftFooter.add(statsBtn);

        statusLabel = new JLabel("Status: Ready ✅");
        statusLabel.setFont(ThemeManager.getBodyFont());
        statusLabel.setForeground(ThemeManager.getSubtext());

        footerPanel.add(leftFooter, BorderLayout.WEST);
        footerPanel.add(statusLabel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setStatus(String msg) {
        statusLabel.setText("Status: " + msg);
        Timer timer = new Timer(3000, e -> statusLabel.setText("Status: Ready ✅"));
        timer.setRepeats(false);
        timer.start();
    }

    private void processCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show Processing dialog
        JDialog processing = new JDialog(this, "Processing", true);
        processing.setSize(300, 150);
        processing.setLocationRelativeTo(this);
        processing.setLayout(new BorderLayout());
        JLabel procLbl = new JLabel("Processing payment...", SwingConstants.CENTER);
        procLbl.setFont(ThemeManager.getTitleFont());
        processing.add(procLbl, BorderLayout.CENTER);

        Timer timer = new Timer(2000, e -> {
            processing.dispose();
            
            // Finalize checkout
            Transaction txn = new Transaction(cart.getItems(), cart.getTotal(), (String)paymentCombo.getSelectedItem(), true);
            Transaction.addTransaction(txn);

            // Deduct stock
            for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
                entry.getKey().reduceStock(entry.getValue());
                // Low stock alert
                if (entry.getKey().getQuantity() < 3) {
                    JOptionPane.showMessageDialog(this, "Low Stock Alert: " + entry.getKey().getName(), "Inventory Alert", JOptionPane.WARNING_MESSAGE);
                }
            }

            cart.clearCart();
            updateCartUI();
            filterByCategory(currentCategory); // refresh grid to update stock UI
            
            ReceiptPanel receiptDialog = new ReceiptPanel(this, txn);
            receiptDialog.setVisible(true);

            setStatus("Checkout successful!");
        });
        timer.setRepeats(false);
        timer.start();
        processing.setVisible(true);
    }

    private void showAISuggestionDialog() {
        JDialog aiDialog = new JDialog(this, "🤖 AI Suggestion", true);
        aiDialog.setSize(600, 500);
        aiDialog.setLocationRelativeTo(this);
        aiDialog.setLayout(new BorderLayout(10, 10));
        aiDialog.getContentPane().setBackground(ThemeManager.getBg());

        JPanel topPnl = new JPanel(new FlowLayout());
        topPnl.setOpaque(false);
        JLabel moodLbl = new JLabel("How are you feeling?");
        moodLbl.setFont(ThemeManager.getBodyFont());
        moodLbl.setForeground(ThemeManager.getText());
        String[] moods = {"Hungry 😋", "Thirsty 💧", "Tired ☕", "Health-conscious 🥗", "Stressed 😫", "Random 🎲"};
        JComboBox<String> moodCombo = new JComboBox<>(moods);
        moodCombo.setFont(ThemeManager.getBodyFont());
        JButton getBtn = createStyledButton("Get Suggestion", ThemeManager.getAccent());
        
        topPnl.add(moodLbl);
        topPnl.add(moodCombo);
        topPnl.add(getBtn);

        JTextArea responseArea = new JTextArea("Select your mood and click 'Get Suggestion'...");
        responseArea.setEditable(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setFont(ThemeManager.getBodyFont());
        responseArea.setBackground(ThemeManager.getCardBg());
        responseArea.setForeground(ThemeManager.getText());
        responseArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(responseArea);

        JPanel suggestGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        suggestGrid.setOpaque(false);

        getBtn.addActionListener(e -> {
            responseArea.setText("Thinking... 🤔");
            getBtn.setEnabled(false);
            
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return AIRecommendation.getRecommendation((String)moodCombo.getSelectedItem(), cart, inventory.getAvailableProducts());
                }
                @Override
                protected void done() {
                    try {
                        String rec = get();
                        responseArea.setText(rec);
                        suggestGrid.removeAll();
                        ArrayList<Product> suggested = AIRecommendation.getTopRecommended(inventory, (String)moodCombo.getSelectedItem());
                        for (Product p : suggested) {
                            suggestGrid.add(new ProductCard(p, VendingMachineGUI.this));
                        }
                        aiDialog.revalidate();
                        aiDialog.repaint();
                    } catch (Exception ex) {
                        responseArea.setText("Error getting suggestion.");
                    }
                    getBtn.setEnabled(true);
                }
            };
            worker.execute();
        });

        aiDialog.add(topPnl, BorderLayout.NORTH);
        aiDialog.add(scroll, BorderLayout.CENTER);
        aiDialog.add(new JScrollPane(suggestGrid), BorderLayout.SOUTH);
        aiDialog.setVisible(true);
    }

    private void showAdminInventory() {
        JDialog dialog = new JDialog(this, "📦 Inventory Management", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"ID", "Name", "Category", "Price (Rs.)", "Stock", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(ThemeManager.getBodyFont());
        table.setRowHeight(25);
        table.getTableHeader().setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));

        Runnable loadData = () -> {
            model.setRowCount(0);
            for (Product p : inventory.getAllProducts()) {
                String status = p.getQuantity() > 5 ? "Good" : (p.getQuantity() >= 2 ? "Low" : "Critical");
                model.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), status});
            }
        };
        loadData.run();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton restockBtn = createStyledButton("Restock Selected", ThemeManager.getPrimary());
        JButton exportBtn = createStyledButton("Export Inventory", ThemeManager.getSuccess());

        restockBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                String qtyStr = JOptionPane.showInputDialog(dialog, "Enter quantity to add:");
                try {
                    int qty = Integer.parseInt(qtyStr);
                    inventory.restockProduct(id, qty);
                    loadData.run();
                    filterByCategory(currentCategory); // refresh main grid
                } catch (NumberFormatException ex) {
                    // Ignore empty or invalid
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a product to restock.");
            }
        });

        exportBtn.addActionListener(e -> {
            try (FileWriter fw = new FileWriter("inventory.txt", java.nio.charset.StandardCharsets.UTF_8)) {
                for (Product p : inventory.getAllProducts()) {
                    fw.write(p.getId() + " | " + p.getName() + " | Stock: " + p.getQuantity() + "\n");
                }
                JOptionPane.showMessageDialog(dialog, "Exported to inventory.txt");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Export failed: " + ex.getMessage());
            }
        });

        btnPanel.add(restockBtn);
        btnPanel.add(exportBtn);

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showTransactionHistory() {
        JDialog dialog = new JDialog(this, "🧾 Transaction History", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"TXN ID", "Date/Time", "Total (Rs.)", "Payment", "Status", "Items Count"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(ThemeManager.getBodyFont());
        table.setRowHeight(25);
        table.getTableHeader().setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));

        for (Transaction t : Transaction.history) {
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int itemsCount = 0;
            for(int q : t.getPurchasedItems().values()) itemsCount += q;
            model.addRow(new Object[]{
                t.getTransactionId(),
                t.getTimestamp().format(fmt),
                String.format("%.2f", t.getTotal()),
                t.getPaymentMethod(),
                t.isSuccessful() ? "Success" : "Failed",
                itemsCount
            });
        }

        JButton viewBtn = createStyledButton("View Receipt", ThemeManager.getPrimary());
        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String id = (String) table.getValueAt(row, 0);
                Transaction found = null;
                for (Transaction t : Transaction.history) {
                    if (t.getTransactionId().equals(id)) {
                        found = t; break;
                    }
                }
                if (found != null) {
                    new ReceiptPanel(this, found).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a transaction.");
            }
        });

        JPanel btnPnl = new JPanel();
        btnPnl.add(viewBtn);

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(btnPnl, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showStats() {
        JDialog dialog = new JDialog(this, "📊 Statistics", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(ThemeManager.getBg());

        int totalTxns = Transaction.history.size();
        double totalRevenue = 0;
        for (Transaction t : Transaction.history) {
            if(t.isSuccessful()) totalRevenue += t.getTotal();
        }

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(20,20,20,20));

        JLabel title = new JLabel("Machine Statistics");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getText());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel txns = new JLabel("Total Transactions: " + totalTxns);
        txns.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD, 18f));
        txns.setForeground(ThemeManager.getText());
        txns.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rev = new JLabel(String.format("Total Revenue: Rs. %.2f", totalRevenue));
        rev.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD, 18f));
        rev.setForeground(ThemeManager.getSuccess());
        rev.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel invValue = new JLabel(inventory.getInventorySummary());
        invValue.setFont(ThemeManager.getBodyFont());
        invValue.setForeground(ThemeManager.getSubtext());
        invValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnl.add(title);
        pnl.add(Box.createRigidArea(new Dimension(0,20)));
        pnl.add(txns);
        pnl.add(Box.createRigidArea(new Dimension(0,10)));
        pnl.add(rev);
        pnl.add(Box.createRigidArea(new Dimension(0,20)));
        pnl.add(invValue);

        dialog.add(pnl, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void toggleTheme() {
        ThemeManager.toggleTheme(this);
        // Refresh full UI
        SwingUtilities.updateComponentTreeUI(this);
        getContentPane().setBackground(ThemeManager.getBg());
        
        // Re-setup to apply colors immediately to custom components
        // In a real app we'd iterate and set background/foreground, but for this exercise
        // we can just re-apply styles or rely on updateComponentTreeUI and repaint.
        // Easiest is to close and re-open or just refresh grid.
        filterByCategory(currentCategory);
        updateCartUI();
        repaint();
    }
}
