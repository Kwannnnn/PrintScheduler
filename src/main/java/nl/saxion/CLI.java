package nl.saxion;

import nl.saxion.model.newModel.SystemFacade;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class CLI implements PropertyChangeListener {
    private static final int MENU_OPTIONS = 9;

    private final SystemFacade model;
    private final PrinterSystemController printerSystemController;
    private final Scanner scanner;

    public CLI(SystemFacade model, PrinterSystemController printerSystemController) {
        this.scanner = new Scanner(System.in);
        this.model = model;
        this.printerSystemController = printerSystemController;
        model.addListener(this);
    }

    public void run() {
        int choice = 1;
        while (choice >= 1 && choice <= MENU_OPTIONS) {
            displayMenu();
            choice = menuChoice(MENU_OPTIONS);
            this.handleChoice(choice);
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> addNewPrintTask();
            case 2 -> registerCompletion();
            case 3 -> registerPrinterFailure();
            case 4 -> changePrintingStyle();
            case 5 -> startPrintQueue();
            case 6 -> showPrints();
            case 7 -> showPrinters();
            case 8 -> showSpools();
            case 9 -> showPendingPrintTasks();
        }
    }

    /**
     * 8)
     */
    private void showSpools() {
        System.out.println("---------- Spools ----------");
        for (String spool : this.model.getSpools()) {
            System.out.println(spool);
        }
        System.out.println("----------------------------");
    }

    /**
     * 6)
     */
    private void showPrints() {
        System.out.println("---------- Available prints ----------");
        for (String print : this.model.getPrints()) {
            System.out.println(print);
        }
        System.out.println("--------------------------------------");
    }

    /**
     * 7)
     */
    private void showPrinters() {
        System.out.println("--------- Available printers ---------");
        for (String printer : this.model.getPrinters()) {
            System.out.println(printer);
        }
        System.out.println("--------------------------------------");
    }

    private void showCurrentlyRunningPrinters(List<String> printers) {
        System.out.println("---------- Currently Running Printers ----------");
        for (String printer : printers) {
            System.out.println(printer);
        }
    }

    public void registerCompletion() {
        List<String> printers = this.model.getRunningPrinters();
        showCurrentlyRunningPrinters(printers);

        displayPrompt("Printer that is done (ID)");
        int printerId = numberInputBetweenBounds(1, printers.size());

        this.printerSystemController.registerCompletion(printerId);
    }

    private void registerPrinterFailure() {
        List<String> printers = this.model.getRunningPrinters();
        showCurrentlyRunningPrinters(printers);

        displayPrompt("Printer ID that failed");
        int printerId = numberInputBetweenBounds(1, printers.size());

        this.printerSystemController.registerPrinterFailure(printerId);
    }

    private void changePrintingStyle() {

    }


    private void startPrintQueue() {
        this.printerSystemController.startPrintQueue();
    }


    private void showPendingPrintTasks() {
        List<String> printTasks = this.model.getPendingPrintTasks();
        System.out.println("--------- Pending Print Tasks ---------");
        for (String p : printTasks) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------");
    }

    private void displaySpools(String filamentType, List<String> availableColors) {
        System.out.println("---------- Spools ----------");
        for (int i = 0; i < availableColors.size(); i++) {
            System.out.println(i+1 + ": " + availableColors.get(i) + " (" + filamentType + ")");
        }
        System.out.println("----------------------------");
    }

    private void addNewPrintTask() {
        // Choose the print to be printed
        List<String> printNames = this.model.getPrintNames();
        displayAvailablePrints(printNames);
        displayPrompt("Print number");
        int printChoice = numberInputBetweenBounds(1, printNames.size()) - 1;

        // Choose a filament
        List<String> filaments = this.model.getFilaments();
        displayFilamentTypes(filaments);
        displayPrompt("Filament type number");
        int filamentChoice = numberInputBetweenBounds(1, filaments.size());

        // Choose a color
        List<String> chosenColors = new ArrayList<>();

        int colorsSize = this.model.getPrintColorsSize(printChoice);
        List<String> availableColors = this.model.getAvailableColors(filamentChoice);

        displaySpools(filaments.get(filamentChoice - 1), availableColors);

        for (int i = 0; i < colorsSize; i++) {
            displayPrompt("Color number");
            int colorChoice = numberInputBetweenBounds(1, availableColors.size()) - 1;
            chosenColors.add(availableColors.get(colorChoice));
        }

        this.printerSystemController.addNewPrintTask(printChoice, filamentChoice, chosenColors);
    }

    private int menuChoice(int max) {
        int choice = -1;
        while (choice < 0 || choice > max) {
            displayPrompt("Choose an option");
            try {
                choice = this.scanner.nextInt();
            } catch (InputMismatchException e) {
                // try again after consuming the current line
                System.out.println("Error: Invalid input");
                this.scanner.nextLine();
            }
        }
        return choice;
    }

    private int numberInput() {
        return scanner.nextInt();
    }

    private int numberInputBetweenBounds(int min, int max) {
        int input = numberInput();
        while (input < min || input > max) {
            input = numberInput();
        }
        return input;
    }

    public void displayMenu() {
        System.out.println("Print Manager");
        System.out.println("=============");
        System.out.println("1) Add new Print Task");
        System.out.println("2) Register Printer Completion");
        System.out.println("3) Register Printer Failure");
        System.out.println("4) Change printing style");
        System.out.println("5) Start Print Queue");
        System.out.println("6) Show prints");
        System.out.println("7) Show printers");
        System.out.println("8) Show spools");
        System.out.println("9) Show pending print tasks");
        System.out.println("0) Exit");
    }

    private void displayAvailablePrints(List<String> prints) {
        System.out.println("---------- Available prints ----------");
        int counter = 1;
        for (var printName : prints) {
            System.out.println(counter + ": " + printName);
            counter++;
        }
        System.out.println("--------------------------------------");
    }

    private void displayPrints(List<String> prints) {
        System.out.println("---------- Available prints ----------");
        for (String print : prints) {
            System.out.println(print);
        }
        System.out.println("--------------------------------------");
    }

    private void displayFilamentTypes(List<String> filaments) {
        System.out.println("---------- Filament Type ----------");
        for (int i = 0; i < filaments.size(); i++) {
            System.out.println(i + 1 + ": " + filaments.get(i));
        }
    }

    private void displayPrompt(String text) {
        System.out.print(text + ": ");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "error", "instruction" -> System.out.println(evt.getNewValue());
        }
    }
}
