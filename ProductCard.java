

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Custom JPanel to display a single product.
 */
public class ProductCard extends JPanel {
    private Product product;
    private boolean isHovered = false;
    private VendingMachineGUI mainGUI;

    public ProductCard(Product product, VendingMachineGUI mainGUI) {
        this.product = product;
        this.mainGUI = mainGUI;

        setPreferredSize(new Dimension(220, 300));
        setMinimumSize(new Dimension(220, 300));
        setMaximumSize(new Dimension(220, 300));
        setLayout(new BorderLayout());
        setOpaque(false); // Make transparent so we can draw custom rounded background
        
        setupUI();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                repaint();
            }
        });
    }

    private void setupUI() {
        removeAll();
        boolean outOfStock = !product.isAvailable();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Emoji
        JLabel emojiLabel = new JLabel(product.getEmoji(), SwingConstants.CENTER);
        emojiLabel.setFont(ThemeManager.getEmojiFont());
        emojiLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Name
        JLabel nameLabel = new JLabel("<html><div style='text-align: center;'>" + product.getName() + "</div></html>");
        nameLabel.setFont(ThemeManager.getTitleFont().deriveFont(Font.BOLD, 16f));
        nameLabel.setForeground(ThemeManager.getText());
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Category Badge
        JLabel catLabel = new JLabel(product.getCategory());
        catLabel.setFont(ThemeManager.getSmallFont());
        catLabel.setForeground(ThemeManager.getPrimary());
        catLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("Rs. %.0f", product.getPrice()));
        priceLabel.setFont(ThemeManager.getPriceFont());
        priceLabel.setForeground(ThemeManager.getAccent());
        priceLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Description
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + product.getDescription() + "</div></html>");
        descLabel.setFont(ThemeManager.getSmallFont());
        descLabel.setForeground(ThemeManager.getSubtext());
        descLabel.setAlignmentX(CENTER_ALIGNMENT);
        descLabel.setPreferredSize(new Dimension(180, 40));

        // Add to Cart Button
        JButton addButton = new JButton(outOfStock ? "OUT OF STOCK" : "+ Add to Cart");
        addButton.setFont(ThemeManager.getBodyFont().deriveFont(Font.BOLD));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(outOfStock ? ThemeManager.getSubtext() : ThemeManager.getPrimary());
        addButton.setFocusPainted(false);
        addButton.setAlignmentX(CENTER_ALIGNMENT);
        addButton.setEnabled(!outOfStock);
        addButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        addButton.addActionListener(e -> {
            if (!outOfStock) {
                mainGUI.addToCart(product);
            }
        });

        // Assemble
        contentPanel.add(emojiLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(catLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(priceLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(addButton);

        // Stock Indicator Bar
        JPanel stockPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int qty = product.getQuantity();
                Color stockColor = qty > 5 ? ThemeManager.getSuccess() : (qty >= 2 ? ThemeManager.getAccent() : ThemeManager.getDanger());
                g.setColor(stockColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        stockPanel.setPreferredSize(new Dimension(220, 5));
        stockPanel.setOpaque(false);

        add(contentPanel, BorderLayout.CENTER);
        add(stockPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow and scale effect on hover
        int x = isHovered ? 2 : 5;
        int y = isHovered ? 2 : 5;
        int w = getWidth() - (isHovered ? 4 : 10);
        int h = getHeight() - (isHovered ? 4 : 10);

        // Shadow
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fill(new RoundRectangle2D.Float(x + 3, y + 3, w, h, 20, 20));

        // Background
        g2.setColor(ThemeManager.getCardBg());
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, 20, 20));

        // Border Glow
        if (isHovered) {
            g2.setColor(ThemeManager.getPrimary());
            g2.draw(new RoundRectangle2D.Float(x, y, w, h, 20, 20));
        } else {
            g2.setColor(ThemeManager.getBorder());
            g2.draw(new RoundRectangle2D.Float(x, y, w, h, 20, 20));
        }

        // Out of stock overlay
        if (!product.isAvailable()) {
            g2.setColor(new Color(ThemeManager.getBg().getRed(), ThemeManager.getBg().getGreen(), ThemeManager.getBg().getBlue(), 150));
            g2.fill(new RoundRectangle2D.Float(x, y, w, h, 20, 20));
        }

        g2.dispose();
    }
    
    public void updateTheme() {
        setupUI();
        repaint();
    }
}
