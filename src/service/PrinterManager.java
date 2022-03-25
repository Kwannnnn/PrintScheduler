package service;

import model.Command;
import model.PrintStatus;

public class PrinterManager {
    public static void handleCommand(Command command) {
        System.out.println("PrinterManager: Executing Command");
        System.out.println("PrinterManager: Print status sent to REStService");
        PrintStatus printStatus = new PrintStatus(command.getMessage(), "FINISHED");
        RESTService.handlePrintStatus(printStatus);
    }
}
