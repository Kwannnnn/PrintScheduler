package nl.saxion;

import nl.saxion.controller.CLI;
import nl.saxion.view.ConsoleView;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    private String printStrategy = "Less Spool Changes";

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        try {
            SystemFacade model = new SystemFacade();
            ConsoleView view = new ConsoleView(System.out);
            CLI commandLineInterface = new CLI(model, view);
            model.addPropertyChangeListener(view);
            commandLineInterface.start();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
//
//    private void startPrintQueue() {
//        manager.startInitialQueue();
//    }
//
//    private void exit() {
//
//    }
//
//    // This method only changes the name but does not actually work.
//    // It exists to demonstrate the output.
//    // in the future strategy might be added.
//    private void changePrintStrategy() {
//        System.out.println("Current strategy: " + printStrategy);
//        System.out.println("1: Less Spool Changes");
//        System.out.println("2: Efficient Spool Usage");
//        System.out.println("Choose strategy: ");
//        int strategyChoice = numberInput(1, 2);
//        if(strategyChoice == 1) {
//            printStrategy = "Less Spool Changes";
//        } else if( strategyChoice == 2) {
//            printStrategy = "Efficient Spool Usage";
//        }
//    }
//
//    // TODO: This should be based on which printer is finished printing.
//    private void registerPrintCompletion() {
//        ArrayList<Printer> printers = manager.getPrinters();
//        System.out.println("---------- Currently Running Printers ----------");
//        for(Printer p: printers) {
//            PrintTask printerCurrentTask= manager.getPrinterCurrentTask(p);
//            if(printerCurrentTask != null) {
//                System.out.println(p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
//            }
//        }
//        System.out.print("Printer that is done (ID): ");
//        int printerId = numberInput(1, printers.size());
//        manager.registerCompletion(printerId);
//    }
//
//    private void registerPrinterFailure() {
//        ArrayList<Printer> printers = manager.getPrinters();
//        System.out.println("---------- Currently Running Printers ----------");
//        for(Printer p: printers) {
//            PrintTask printerCurrentTask= manager.getPrinterCurrentTask(p);
//            if(printerCurrentTask != null) {
//                System.out.println(p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
//            }
//        }
//        System.out.print("Printer ID that failed: ");
//        int printerId = numberInput(1, printers.size());
//        manager.registerPrinterFailure(printerId);
//    }
//
//    private void addNewPrintTask() {
//        List<String> colors = new ArrayList<>();
//        var prints = manager.getPrints();
//        System.out.println("---------- Available prints ----------");
//        int counter = 1;
//        for (var p : prints) {
//            System.out.println(counter + ": " + p.getName());
//            counter++;
//        }
//        System.out.println("--------------------------------------");
//        System.out.print("Print number: ");
//        int printNumber = numberInput(1, prints.size());
//        Print print = manager.findPrint(printNumber - 1);
//        String printName = print.getName();
//        System.out.println("---------- Filament Type ----------");
//        System.out.println("1: PLA");
//        System.out.println("2: PETG");
//        System.out.println("3: ABS");
//        System.out.print("Filament type number: ");
//        int ftype = numberInput(1, 3);
//        FilamentType type;
//        switch (ftype) {
//            case 1:
//                type = FilamentType.PLA;
//                break;
//            case 2:
//                type = FilamentType.PETG;
//                break;
//            case 3:
//                type = FilamentType.ABS;
//                break;
//            default:
//                System.out.println("Not a valid filamentType, bailing out");
//                return;
//        }
//        var spools = manager.getSpools();
//        System.out.println("---------- Spools ----------");
//        ArrayList<String> availableColors = new ArrayList<>();
//        counter = 1;
//        for (var spool : spools) {
//            String colorString = spool.getColor();
//            if(type == spool.getFilamentType() && !availableColors.contains(colorString)) {
//                System.out.println(counter + ": " + colorString + " (" + spool.getFilamentType() + ")");
//                availableColors.add(colorString);
//                counter++;
//            }
//        }
//        System.out.println("----------------------------");
//        System.out.print("Color number: ");
//        int colorChoice = numberInput(1, availableColors.size());
//        colors.add(availableColors.get(colorChoice-1));
//        for(int i = 1; i < print.getFilamentLength().size(); i++) {
//            System.out.print("Color number: ");
//            colorChoice = numberInput(1, availableColors.size());
//            colors.add(availableColors.get(colorChoice-1));
//        }
//        manager.addPrintTask(printName, colors, type);
//    }
}
