package nl.saxion.controller;

import nl.saxion.SystemFacade;
import nl.saxion.view.ConsoleView;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CLI {
    private final SystemFacade model;
    private final ConsoleView view;
    private final Scanner scanner;

    public CLI(SystemFacade model, ConsoleView view) {
        this.model = model;
        this.view = view;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        commandLoop();
    }

    private void commandLoop() {
        int choice = 1;
        while (choice > 0 && choice < 10) {
            this.view.displayMenu();
            choice = menuChoice(9);
            switch (choice) {
                case 1 -> addNewPrintTask();
                case 2 -> registerPrintCompletion();
                case 3 -> registerPrinterFailure();
                case 4 -> changePrintStrategy();
                case 5 -> this.model.startPrintQueue();
                case 6 -> this.model.showPrints();
                case 7 -> this.model.showPrinters();
                case 8 -> this.model.showSpools();
                case 9 -> this.model.showPendingPrintTasks();
            }
        }
    }

    private void changePrintStrategy() {
    }

    private void registerPrinterFailure() {
    }

    private void addNewPrintTask() {
        this.model.showPrintNamesNumbered();
        this.view.displayPrompt("Print number: ");
        int printId;
        do {
            printId = this.scanner.nextInt();
        } while (!this.model.printWithIdExists(printId));
        this.model.showFilamentTypesNumbered();
        this.view.displayPrompt("Filament type number: ");
        // TODO: validate input
        var ftype = this.scanner.nextInt();
        this.model.showSpoolsOfFilamentType(ftype);
    }

    private void registerPrintCompletion() {
        this.model.showCurrentlyRunningPrinters();
        this.view.displayLine("Printer that is done (ID): ");
        // TODO: printers.size()
        int printerId = numberInput(1, 4);
        this.model.registerCompletion(printerId);
    }

    public String stringInput() {
        String input = null;
        while(input == null || input.length() == 0){
            input = scanner.nextLine();
        }
        return input;
    }

    public int numberInput() {
        int input = scanner.nextInt();
        return input;
    }

    public int numberInput(int min, int max) {
        int input = numberInput();
        while (input < min || input > max) {
            input = numberInput();
        }
        return input;
    }

    public int menuChoice(int max) {
        int choice = -1;
        while (choice < 0 || choice > max) {
            System.out.print("Choose an option: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                //try again after consuming the current line
                System.out.println("Error: Invalid input");
                scanner.nextLine();
            }
        }
        return choice;
    }
}
