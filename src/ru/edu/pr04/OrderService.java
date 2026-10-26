package ru.edu.pr04;

public class OrderService {
    public Order createOrder(Employee employee) {
        return new Order(employee);
    }

    public void validateForConfirm(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Заказ не задан");
        }
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Заказ уже подтверждён");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Нельзя подтвердить пустой заказ");
        }
    }

    public void confirmOrder(Order order) {
        validateForConfirm(order);
        order.confirm();
    }
}
