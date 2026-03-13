import java.util.*;
import java.io.*;

public class Store {
    private Map<String, Item> inventory;
    private List<CartItem> cart;
    private String transactionFile;
    private final double TAX_RATE = 0.06;

    public Store(String inventoryPath, String transactionPath) {
        this.inventory = new HashMap<>();
        this.cart = new ArrayList<>();
        this.transactionFile = transactionPath;
        loadInventory(inventoryPath);
        initializeTransactionFile();
    }

    private void loadInventory(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0];
                    String desc = parts[1].replaceAll("\"", "");
                    String status = parts[2];
                    int qty = Integer.parseInt(parts[3]);
                    double price = Double.parseDouble(parts[4]);
                    inventory.put(id, new Item(id, desc, status, qty, price));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    private void initializeTransactionFile() {
        File file = new File(transactionFile);
        if (!file.exists()) {
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                out.println("TransactionID,DateTime,ItemID,ItemName,Quantity,ItemTotal,OrderSubtotal,Tax,OrderTotal");
            } catch (IOException e) {
                System.err.println("Error initializing transaction file: " + e.getMessage());
            }
        }
    }

    public Item searchItem(String id) {
        return inventory.get(id);
    }

    public boolean addItemToCart(String id, int quantity) {
        Item item = inventory.get(id);
        if (item == null || item.getQuantity() < quantity || !item.getStockStatus().equalsIgnoreCase("in stock")) {
            return false;
        }
        item.reduceStock(quantity);
        cart.add(new CartItem(item, quantity));
        return true;
    }

    public double getSubtotal() {
        return cart.stream().mapToDouble(CartItem::getTotal).sum();
    }

    public boolean removeLastItem() {
        if (!cart.isEmpty()) {
            cart.remove(cart.size() - 1);
            return true;
        }
        return false;
    }

    public void checkout() {
        String transactionId = Utils.generateTransactionId();
        String timestamp = Utils.getTimestamp();
        double subtotal = getSubtotal();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        try (PrintWriter out = new PrintWriter(new FileWriter(transactionFile, true))) {
            for (CartItem item : cart) {
                out.printf("%s,%s,%s,%s,%d,%.2f,%.2f,%.2f,%.2f%n",
                    transactionId,
                    timestamp,
                    item.getItem().getId(),
                    item.getItem().getDescription(),
                    item.getQuantity(),
                    item.getTotal(),
                    subtotal,
                    tax,
                    total
                );
            }
        } catch (IOException e) {
            System.err.println("Error writing transaction: " + e.getMessage());
        }
        cart.clear();
    }

    public void resetCart() {
        cart.clear();
    }

    public List<String> getCartSummary() {
        List<String> lines = new ArrayList<>();
        for (CartItem c : cart) {
            lines.add(c.toString());
        }
        return lines;
    }

    public int getCartSize() {
        return cart.size();
    }
}
