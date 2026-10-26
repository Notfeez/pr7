package ru.edu.pr04;

public class OrderItem {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 100;
    public static final int MAX_MERGED_QUANTITY = 35;

    private final Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Товар не задан");
        }
        this.product = product;
        setQuantity(quantity);
    }

    private void setQuantity(int quantity) {
        checkQuantity(quantity);
        this.quantity = quantity;
    }

    private void checkQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Количество должно быть от " + MIN_QUANTITY + " до " + MAX_QUANTITY + ": " + quantity);
        }
    }

    public void increase(int delta) {
        if (delta < 1) {
            throw new IllegalArgumentException("Добавляемое количество должно быть положительным: " + delta);
        }
        long sum = (long) quantity + delta;
        if (sum > MAX_MERGED_QUANTITY) {
            throw new IllegalArgumentException("Объединённое количество не может быть больше " + MAX_MERGED_QUANTITY + ": " + sum);
        }
        quantity = (int) sum;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}
