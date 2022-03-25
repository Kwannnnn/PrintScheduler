package service;

import model.Command;
import model.Query;

public class PrinterService {

    public static void handleCommand(Command command) {
        sendQueryToDatabase(command);
        sendCommandToPrinterManager(command);
    }

    private static void sendQueryToDatabase(Command command) {
        Query query = new Query(command.getMessage());
        System.out.println("PrinterService: Query sent to DatabaseService");
        DatabaseService.handleQuery(query);
    }

    private static void sendCommandToPrinterManager(Command command) {
        System.out.println("PrinterService: Command sent to PrinterManager");
        PrinterManager.handleCommand(command);
    }
}
