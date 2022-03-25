package service;

import model.Command;
import model.Order;
import model.Query;

public class OrderService {

    public static void handleNewOrder(Order order) {
        sendQueryToDatabase(order);
        sendCommandToPrinterService(order);
    }

    private static void sendCommandToPrinterService(Order order) {
        Command command = new Command(order.getMessage());
        System.out.println("OrderService: Command sent to PrinterService");
        PrinterService.handleCommand(command);

    }

    private static void sendQueryToDatabase(Order order) {
        Query query = new Query(order.getMessage());
        System.out.println("OrderService: Query sent to DatabaseService");
        DatabaseService.handleQuery(query);
    }
}
