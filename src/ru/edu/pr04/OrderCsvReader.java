package ru.edu.pr04;

public class OrderCsvReader {
    public void addLine(Order order, String line) {
        if (order == null) {
            throw new IllegalArgumentException("Заказ не задан");
        }
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Пустая строка");
        }
        String[] parts = line.split(";", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Ожидалось 4 поля, получено " + parts.length);
        }
        double price = parsePrice(parts[2].trim());
        int quantity = parseQuantity(parts[3].trim());
        order.addProduct(new Product(parts[0], parts[1], price), quantity);
    }

    private double parsePrice(String text) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Цена не задана");
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Цена не является числом: " + text);
        }
    }

    private int parseQuantity(String text) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Количество не задано");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Количество не является целым числом: " + text);
        }
    }
}
