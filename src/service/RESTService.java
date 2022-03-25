package service;

import model.Command;
import model.Order;
import model.PrintStatus;
import model.Request;

public class RESTService {

    public static void handlePrintStatus(PrintStatus printStatus) {
        System.out.println("RESTService: PrintStatus sent to NotificationService");
        NotificationService.handlePrintStatus(printStatus);
    }

    public static void handleOrderRequest(Request request) {
        Order order = new Order(request.getMessage());
        System.out.println("RESTService: Order sent to OrderService");
        OrderService.handleNewOrder(order);
    }

    public static void handleTabletRequest(Request request) {
        Command command = new Command(request.getMessage());
        System.out.println("RESTService: Command sent to PrinterService");
        PrinterService.handleCommand(command);
    }
}
