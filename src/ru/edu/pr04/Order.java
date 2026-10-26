package ru.edu.pr04;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final Employee employee;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Сотрудник не задан");
        }
        this.employee = employee;
    }

    public void addProduct(Product product, int quantity) {
        checkDraft();
        if (product == null) {
            throw new IllegalArgumentException("Товар не задан");
        }
        OrderItem item = findItem(product.getId());
        if (item == null) {
            items.add(new OrderItem(product, quantity));
            return;
        }
        checkSameProduct(item.getProduct(), product);
        item.increase(quantity);
    }

    public void removeProduct(String productId) {
        checkDraft();
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Код товара не задан");
        }
        OrderItem item = findItem(productId.trim());
        if (item == null) {
            throw new IllegalArgumentException("В заказе нет товара с кодом " + productId);
        }
        items.remove(item);
    }

    public double total() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.getLineTotal();
        }
        return sum;
    }

    public void confirm() {
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Заказ уже подтверждён");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Нельзя подтвердить пустой заказ");
        }
        status = OrderStatus.CONFIRMED;
    }

    public List<OrderItem> getItems() {
        List<OrderItem> copy = new ArrayList<>();
        for (OrderItem item : items) {
            copy.add(new OrderItem(item.getProduct(), item.getQuantity()));
        }
        return List.copyOf(copy);
    }

    public Employee getEmployee() {
        return employee;
    }

    public OrderStatus getStatus() {
        return status;
    }

    private void checkDraft() {
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Подтверждённый заказ нельзя изменять");
        }
    }

    private OrderItem findItem(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    private void checkSameProduct(Product existing, Product added) {
        if (!existing.getName().equals(added.getName()) || existing.getPrice() != added.getPrice()) {
            throw new IllegalArgumentException("Товар " + added.getId() + " уже есть в заказе с другими данными");
        }
    }
}
