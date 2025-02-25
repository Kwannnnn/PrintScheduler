package nl.saxion.expansion;

import nl.saxion.expansion.model.SystemFacade;

import java.util.List;

public class PrinterSystemController {
    private final SystemFacade model;

    public PrinterSystemController(SystemFacade model) {
        this.model = model;
    }

    public void addNewPrintTask(int printChoice, int filamentChoice, List<String> chosenColors) {
        // TODO: validate input
        this.model.addPrintTask(printChoice, chosenColors, filamentChoice);
    }

    public void changePrintingStyle(int printStyle) {
        this.model.setPrintingStrategy(printStyle);
    }

    public void startPrintQueue() {
        this.model.startInitialQueue();
    }

    public void registerPrinterFailure(int printerId) {
        this.model.registerPrinterFailure(printerId);
    }

    public void registerCompletion(int printerId) {
        this.model.registerCompletion(printerId);
    }
}
