
public class Item {
    private String id;
    private String description;
    private String stockStatus;
    private int quantity;
    private double unitPrice;

    public Item(String id, String description, String stockStatus, int quantity, double unitPrice) {
        this.id = id;
        this.description = description;
        this.stockStatus = stockStatus;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getStockStatus() { return stockStatus; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void reduceStock(int qty) { this.quantity -= qty; }
}