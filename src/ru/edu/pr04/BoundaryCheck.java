package ru.edu.pr04;

import java.util.List;

public class BoundaryCheck {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        Product monitor = new Product("M01", "Monitor 27", 32990);
        Product keyboard = new Product("K01", "Keyboard", 4490);
        int max = OrderItem.MAX_QUANTITY;
        int merged = OrderItem.MAX_MERGED_QUANTITY;

        Order order = newOrder();
        order.addProduct(monitor, 2);
        order.addProduct(keyboard, 3);
        expect("типовой заказ: позиций", order.getItems().size(), 2);
        expect("типовой заказ: итог", order.total(), 32990 * 2 + 4490 * 3.0);

        order = newOrder();
        order.addProduct(monitor, 1);
        expect("количество 1", order.getItems().get(0).getQuantity(), 1);

        order = newOrder();
        order.addProduct(monitor, max);
        expect("количество " + max + " (верхняя граница)", order.getItems().get(0).getQuantity(), max);

        expectError("количество " + (max + 1), IllegalArgumentException.class, () -> newOrder().addProduct(monitor, max + 1));
        expectError("количество 0", IllegalArgumentException.class, () -> newOrder().addProduct(monitor, 0));
        expectError("количество -1", IllegalArgumentException.class, () -> newOrder().addProduct(monitor, -1));
        expectError("количество Integer.MAX_VALUE", IllegalArgumentException.class, () -> newOrder().addProduct(monitor, Integer.MAX_VALUE));

        order = newOrder();
        order.addProduct(monitor, 2);
        order.addProduct(monitor, 1);
        expect("повтор товара: одна строка", order.getItems().size(), 1);
        expect("повтор товара: количество", order.getItems().get(0).getQuantity(), 3);

        order = newOrder();
        order.addProduct(monitor, merged - 5);
        order.addProduct(monitor, 5);
        expect("объединение до " + merged, order.getItems().get(0).getQuantity(), merged);

        Order mergeLimit = newOrder();
        mergeLimit.addProduct(monitor, merged - 5);
        expectError("объединение до " + (merged + 1), IllegalArgumentException.class, () -> mergeLimit.addProduct(monitor, 6));
        expect("после отказа количество прежнее", mergeLimit.getItems().get(0).getQuantity(), merged - 5);

        Order several = newOrder();
        several.addProduct(monitor, 10);
        several.addProduct(monitor, 10);
        several.addProduct(monitor, 10);
        several.addProduct(monitor, 5);
        expect("несколько повторов подряд", several.getItems().get(0).getQuantity(), merged);
        expectError("ещё один повтор сверх " + merged, IllegalArgumentException.class, () -> several.addProduct(monitor, 1));
        expect("после отказа одна строка", several.getItems().size(), 1);

        Order big = newOrder();
        big.addProduct(monitor, merged + 1);
        expect("новая позиция больше " + merged + " допустима", big.getItems().get(0).getQuantity(), merged + 1);
        expectError("объединение позиции больше " + merged, IllegalArgumentException.class, () -> big.addProduct(monitor, 1));

        Order other = newOrder();
        other.addProduct(monitor, merged);
        other.addProduct(keyboard, merged);
        expect("лимит объединения считается по каждому товару отдельно", other.getItems().size(), 2);

        Order conflict = newOrder();
        conflict.addProduct(monitor, 1);
        expectError("тот же id и другая цена", IllegalArgumentException.class, () -> conflict.addProduct(new Product("M01", "Monitor 27", 100), 1));
        expectError("тот же id и другое имя", IllegalArgumentException.class, () -> conflict.addProduct(new Product("M01", "Other", 32990), 1));
        expect("после конфликта заказ не изменился", conflict.getItems().get(0).getQuantity(), 1);

        expectError("товар null", IllegalArgumentException.class, () -> newOrder().addProduct(null, 1));
        expectError("id пустой", IllegalArgumentException.class, () -> new Product("", "A", 10));
        expectError("id только пробелы", IllegalArgumentException.class, () -> new Product("   ", "A", 10));
        expectError("id null", IllegalArgumentException.class, () -> new Product(null, "A", 10));
        expectError("наименование пустое", IllegalArgumentException.class, () -> new Product("A1", "", 10));
        expectError("цена 0", IllegalArgumentException.class, () -> new Product("A1", "A", 0));
        expectError("цена -100", IllegalArgumentException.class, () -> new Product("A1", "A", -100));
        expectError("цена NaN", IllegalArgumentException.class, () -> new Product("A1", "A", Double.NaN));
        expectError("цена бесконечность", IllegalArgumentException.class, () -> new Product("A1", "A", Double.POSITIVE_INFINITY));
        expect("цена 0.01", new Product("A1", "A", 0.01).getPrice(), 0.01);
        expectError("сотрудник null", IllegalArgumentException.class, () -> new Order(null));

        order = newOrder();
        expect("пустой заказ: итог", order.total(), 0.0);
        order.addProduct(monitor, 1);
        order.addProduct(keyboard, 2);
        order.removeProduct("M01");
        expect("после удаления: позиций", order.getItems().size(), 1);
        expect("после удаления: итог пересчитан", order.total(), 4490 * 2.0);
        order.addProduct(monitor, 1);
        expect("удалённый товар можно добавить снова", order.getItems().size(), 2);

        Order removing = newOrder();
        removing.addProduct(monitor, 1);
        expectError("удаление отсутствующего товара", IllegalArgumentException.class, () -> removing.removeProduct("X99"));
        expectError("удаление по пустому id", IllegalArgumentException.class, () -> removing.removeProduct(""));
        expectError("удаление по null", IllegalArgumentException.class, () -> removing.removeProduct(null));

        Order confirmed = newOrder();
        confirmed.addProduct(monitor, 2);
        confirmed.confirm();
        expect("статус после confirm", confirmed.getStatus(), OrderStatus.CONFIRMED);
        expectError("добавление после confirm", IllegalStateException.class, () -> confirmed.addProduct(keyboard, 1));
        expectError("повторное добавление того же товара после confirm", IllegalStateException.class, () -> confirmed.addProduct(monitor, 1));
        expectError("удаление после confirm", IllegalStateException.class, () -> confirmed.removeProduct("M01"));
        expectError("повторный confirm", IllegalStateException.class, () -> confirmed.confirm());
        expect("после confirm итог доступен", confirmed.total(), 32990 * 2.0);
        expectError("confirm пустого заказа", IllegalStateException.class, () -> newOrder().confirm());

        Order copyCheck = newOrder();
        copyCheck.addProduct(monitor, 2);
        List<OrderItem> list = copyCheck.getItems();
        expectError("изменение списка getItems", UnsupportedOperationException.class, () -> list.add(new OrderItem(keyboard, 1)));
        list.get(0).increase(5);
        expect("изменение копии позиции не влияет на заказ", copyCheck.getItems().get(0).getQuantity(), 2);
        copyCheck.confirm();
        copyCheck.getItems().get(0).increase(5);
        expect("подтверждённый заказ не меняется через getItems", copyCheck.getItems().get(0).getQuantity(), 2);

        OrderService service = new OrderService();
        expectError("validateForConfirm(null)", IllegalArgumentException.class, () -> service.validateForConfirm(null));
        expectError("confirmOrder пустого заказа", IllegalStateException.class, () -> service.confirmOrder(newOrder()));
        Order forService = newOrder();
        forService.addProduct(keyboard, 1);
        service.confirmOrder(forService);
        expect("confirmOrder подтверждает заказ", forService.getStatus(), OrderStatus.CONFIRMED);
        expectError("confirmOrder повторно", IllegalStateException.class, () -> service.confirmOrder(forService));

        OrderCsvReader reader = new OrderCsvReader();
        Order fromCsv = newOrder();
        reader.addLine(fromCsv, "M01;Monitor 27;32990;2");
        reader.addLine(fromCsv, "M01;Monitor 27;32990;1");
        expect("csv: объединение повторной строки", fromCsv.getItems().get(0).getQuantity(), 3);
        reader.addLine(fromCsv, " K01 ; Keyboard ; 4490 ; 1 ");
        expect("csv: пробелы вокруг полей", fromCsv.getItems().size(), 2);
        expectError("csv: отрицательная цена", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "X01;Broken;-100;1"));
        expectError("csv: пустая строка", IllegalArgumentException.class, () -> reader.addLine(fromCsv, ""));
        expectError("csv: null", IllegalArgumentException.class, () -> reader.addLine(fromCsv, null));
        expectError("csv: мало полей", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;100"));
        expectError("csv: много полей", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;100;1;2"));
        expectError("csv: пустая цена", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;;1"));
        expectError("csv: цена не число", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;abc;1"));
        expectError("csv: пустое количество", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;100;"));
        expectError("csv: количество не число", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;100;two"));
        expectError("csv: дробное количество", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;100;1.5"));
        expectError("csv: пустой id", IllegalArgumentException.class, () -> reader.addLine(fromCsv, ";Name;100;1"));
        expectError("csv: конфликт цены", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "M01;Monitor 27;1;1"));
        expectError("csv: количество выше максимума", IllegalArgumentException.class, () -> reader.addLine(fromCsv, "A1;Name;100;" + (max + 1)));
        expect("csv: ошибочные строки заказ не изменили", fromCsv.getItems().size(), 2);

        System.out.println("Прошло: " + passed + ", не прошло: " + failed);
    }

    private static Order newOrder() {
        return new OrderService().createOrder(new Employee("E01", "Иванов Иван"));
    }

    private static void expect(String name, Object actual, Object expected) {
        if (actual.equals(expected)) {
            passed++;
            System.out.println("OK   " + name + " -> " + actual);
        } else {
            failed++;
            System.out.println("FAIL " + name + ": ожидалось " + expected + ", получено " + actual);
        }
    }

    private static void expectError(String name, Class<? extends RuntimeException> type, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("FAIL " + name + ": ошибка не возникла");
        } catch (RuntimeException e) {
            if (type.isInstance(e)) {
                passed++;
                System.out.println("OK   " + name + " -> " + e.getMessage());
            } else {
                failed++;
                System.out.println("FAIL " + name + ": другой тип ошибки " + e.getClass().getSimpleName());
            }
        }
    }
}
