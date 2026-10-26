package ru.edu.pr04;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class OrderApp {
    public static void main(String[] args) throws IOException {
        String path = args.length > 0 ? args[0] : "data/sample.csv";
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);

        OrderService service = new OrderService();
        OrderCsvReader reader = new OrderCsvReader();
        Order order = service.createOrder(new Employee("E01", "Иванов Иван"));

        for (int i = 1; i < lines.size(); i++) {
            try {
                reader.addLine(order, lines.get(i));
            } catch (IllegalArgumentException e) {
                System.out.println("Строка " + (i + 1) + " пропущена: " + e.getMessage());
            }
        }

        for (OrderItem item : order.getItems()) {
            System.out.println(item.getProduct().getId() + " " + item.getProduct().getName() + " x" + item.getQuantity() + " = " + item.getLineTotal());
        }
        System.out.println("Итого: " + order.total());

        try {
            service.confirmOrder(order);
            System.out.println("Статус: " + order.getStatus());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Подтвердить не удалось: " + e.getMessage());
        }
    }
}
