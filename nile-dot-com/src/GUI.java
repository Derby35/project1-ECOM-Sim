import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;     



public class GUI {
    private Store store;
    private JFrame frame;
    private JTextField itemIDField, quantityField;
    private JTextArea cartArea;
    private JLabel subtotalLabel, taxLabel, totalLabel, statusLabel;
    private JButton findItemBtn, addToCartBtn, deleteLastBtn, checkOutBtn, newOrderBtn, exitBtn, viewOrdersBtn;

    public GUI() {
        store = new Store("data/inventory.csv", "output/transactions.csv");
    }

    public void createAndShowGUI() {
        frame = new JFrame("Nile Dot Com - E-Store Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel panel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Item Details"));
        itemIDField = new JTextField();
        quantityField = new JTextField();
        inputPanel.add(new JLabel("Item ID:"));
        inputPanel.add(itemIDField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        findItemBtn = new JButton("Find Item");
        addToCartBtn = new JButton("Add to Cart");
        inputPanel.add(findItemBtn);
        inputPanel.add(addToCartBtn);
        panel.add(inputPanel, BorderLayout.NORTH);

        // Cart Area
        cartArea = new JTextArea(10, 50);
        cartArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new GridLayout(4, 2));
        deleteLastBtn = new JButton("Delete Last Item");
        checkOutBtn = new JButton("Check Out");
        newOrderBtn = new JButton("New Order");
        exitBtn = new JButton("Exit");
        viewOrdersBtn = new JButton("View Past Orders");

        subtotalLabel = new JLabel("Subtotal: $0.00");
        taxLabel = new JLabel("Tax (6%): $0.00");
        totalLabel = new JLabel("Total: $0.00");
        statusLabel = new JLabel("Status: Ready");

        actionPanel.add(deleteLastBtn);
        actionPanel.add(checkOutBtn);
        actionPanel.add(newOrderBtn);
        actionPanel.add(exitBtn);
        actionPanel.add(viewOrdersBtn);
        actionPanel.add(subtotalLabel);
        actionPanel.add(taxLabel);
        actionPanel.add(totalLabel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        frame.setContentPane(panel);
        frame.setVisible(true);


        findItemBtn.addActionListener(e -> {
    String itemId = itemIDField.getText().trim();
    Item item = store.searchItem(itemId);

    if (item != null) {
        statusLabel.setText("Status: Item found - " + item.getDescription());
    } else {
        statusLabel.setText("Status: Item not found");
        JOptionPane.showMessageDialog(frame,
            "The item ID '" + itemId + "' is not in the inventory file.",
            "Item Not Found",
            JOptionPane.ERROR_MESSAGE);
    }
});


        addToCartBtn.addActionListener(e -> {
    String itemId = itemIDField.getText().trim();
    String quantityStr = quantityField.getText().trim();

    try {
        int quantity = Integer.parseInt(quantityStr);
        Item item = store.searchItem(itemId);

        if (item == null) {
            statusLabel.setText("Status: Item not found");
            JOptionPane.showMessageDialog(frame,
                "Item ID '" + itemId + "' was not found in the inventory file.",
                "Item Not Found",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (item.getStockStatus().equalsIgnoreCase("out of stock")) {
            statusLabel.setText("Status: Item is out of stock");
            JOptionPane.showMessageDialog(frame,
                "The item '" + item.getDescription() + "' is currently out of stock.",
                "Out of Stock",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantity > item.getQuantity()) {
            statusLabel.setText("Status: Not enough stock");
            JOptionPane.showMessageDialog(frame,
                "Requested quantity (" + quantity + ") exceeds current stock (" + item.getQuantity() + ").",
                "Insufficient Quantity",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = store.addItemToCart(itemId, quantity);
        if (success) {
            refreshCart();
            statusLabel.setText("Status: Item added to cart");
        } else {
            statusLabel.setText("Status: Unable to add item");
        }

    } catch (NumberFormatException ex) {
        statusLabel.setText("Status: Invalid quantity");
        JOptionPane.showMessageDialog(frame,
            "Please enter a valid number for quantity.",
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
    }
});

        deleteLastBtn.addActionListener(e -> {
            boolean success = store.removeLastItem();
            if (success) {
                refreshCart();
                statusLabel.setText("Status: Last item removed");
            } else {
                statusLabel.setText("Status: Cart is empty");
            }
        });

        checkOutBtn.addActionListener(e -> {
            if (store.getCartSize() == 0) {
                statusLabel.setText("Status: Cart is empty");
                return;
            }

            List<String> invoiceLines = store.getCartSummary();
            double subtotal = store.getSubtotal();
            double tax = subtotal * 0.06;
            double total = subtotal + tax;
            String timestamp = Utils.getTimestamp();

            store.checkout(); // Writes to file

            StringBuilder invoice = new StringBuilder();
            invoice.append("       *** Nile Dot Com - Purchase Invoice ***\n");
            invoice.append("Date/Time: ").append(timestamp).append("\n\n");
            invoice.append("Item ID | Title | Qty | Price | Discount | Subtotal\n");
            invoice.append("---------------------------------------------------------\n");

            for (String line : invoiceLines) {
                invoice.append(line).append("\n");
            }

            invoice.append("\n");
            invoice.append(String.format("Order Subtotal: $%.2f\n", subtotal));
            invoice.append("Tax Rate: 6%\n");
            invoice.append(String.format("Tax Amount: $%.2f\n", tax));
            invoice.append(String.format("Order Total: $%.2f\n", total));
            invoice.append("\nThank you for shopping with Nile Dot Com!\n");

            JTextArea invoiceArea = new JTextArea(invoice.toString(), 20, 60);
            invoiceArea.setEditable(false);
            JScrollPane invoiceScroll = new JScrollPane(invoiceArea);
            JOptionPane.showMessageDialog(frame, invoiceScroll, "Purchase Invoice", JOptionPane.INFORMATION_MESSAGE);

            refreshCart();
            statusLabel.setText("Status: Order complete");
        });

        newOrderBtn.addActionListener(e -> {
            store.resetCart();
            refreshCart();
            statusLabel.setText("Status: New order started");
        });

        exitBtn.addActionListener(e -> frame.dispose());

        viewOrdersBtn.addActionListener(e -> {
            JTextArea historyArea = new JTextArea(20, 60);
            historyArea.setEditable(false);
            try (BufferedReader br = new BufferedReader(new FileReader("output/transactions.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    historyArea.append(line + "\n");
                }
            } catch (IOException ex) {
                historyArea.setText("Unable to load transaction history.");
            }

            JScrollPane scroll = new JScrollPane(historyArea);
            JOptionPane.showMessageDialog(frame, scroll, "Transaction History", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void refreshCart() {
        cartArea.setText("Item #: | Name | Qty | Price | Discount | Subtotal\n");
        cartArea.append("-------------------------------------------------------------\n");
        for (String line : store.getCartSummary()) {
            cartArea.append(line + "\n");
        }

        double subtotal = store.getSubtotal();
        double tax = subtotal * 0.06;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        taxLabel.setText(String.format("Tax (6%%): $%.2f", tax));
        totalLabel.setText(String.format("Total: $%.2f", total));
    }
}
