package ru.edu.pr04;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = true;
        try {
            ok = ok && new Product("P1", "Mouse", 1000).getPrice() == 1000;

            Product mouse = new Product("P1", "Mouse", 1000);
            Product keyboard = new Product("P2", "Keyboard", 4490);
            Order order = new OrderService().createOrder(new Employee("E1", "Петров Пётр"));
            order.addProduct(mouse, 2);
            order.addProduct(keyboard, 1);
            order.addProduct(mouse, 3);
            ok = ok && order.getItems().size() == 2;
            ok = ok && order.getItems().get(0).getQuantity() == 5;
            ok = ok && order.total() == 5 * 1000 + 4490;

            order.removeProduct("P2");
            ok = ok && order.getItems().size() == 1;
            ok = ok && order.total() == 5000;

            order.confirm();
            ok = ok && order.getStatus() == OrderStatus.CONFIRMED;
            try {
                order.addProduct(keyboard, 1);
                ok = false;
            } catch (IllegalStateException e) {
                ok = ok && true;
            }
        } catch (Exception e) {
            System.out.println("FAIL exception " + e.getMessage());
            ok = false;
        }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
