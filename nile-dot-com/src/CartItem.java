public class CartItem {
    private Item item;
    private int quantity;
    private double discountRate;

    public CartItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.discountRate = calculateDiscount(quantity);
    }

    //  discount rate 
    private double calculateDiscount(int qty) {
        if (qty >= 15) return 0.20;
        if (qty >= 10) return 0.15;
        if (qty >= 5) return 0.10;
        return 0.0;
    }

    // Total price 
    public double getTotal() {
        return quantity * item.getUnitPrice() * (1 - discountRate);
    }

    @Override
    public String toString() {
        double subtotal = getTotal();
        return String.format(
            "Item #: %s | Name: %s | Qty: %d | Price: $%.2f | Discount: %.0f%% | Subtotal: $%.2f",
            item.getId(),
            item.getDescription(),
            quantity,
            item.getUnitPrice(),
            discountRate * 100,
            subtotal
        );
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}
