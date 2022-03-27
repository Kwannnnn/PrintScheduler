package nl.saxion.expansion.model;

import nl.saxion.expansion.exception.ValidationException;
import nl.saxion.expansion.model.io.FileLoader;
import nl.saxion.expansion.model.io.record.PrintFileRecord;
import nl.saxion.expansion.model.io.record.PrinterFileRecord;
import nl.saxion.expansion.model.io.record.SpoolFileRecord;
import nl.saxion.expansion.model.manager.PrintManager;
import nl.saxion.expansion.model.manager.PrinterManager;
import nl.saxion.expansion.model.manager.SpoolManager;
import nl.saxion.expansion.model.manager.TaskManager;
import nl.saxion.expansion.model.strategy.EfficientSpoolUsageStrategy;
import nl.saxion.expansion.model.strategy.LessSpoolChangeStrategy;
import nl.saxion.expansion.model.strategy.PrintingStrategy;
import nl.saxion.expansion.model.visitor.*;
import org.json.simple.parser.ParseException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.*;

public class SystemFacade {
    private final PropertyChangeSupport propertyChangeSupport;
    private final PrintManager printManager;
    private final SpoolManager spoolManager;
    private final PrinterManager printerManager;
    private final TaskManager taskManager;
    private final List<PrintingStrategy> printingStrategies;
    private PrintingStrategy printingStrategy;

    public SystemFacade(FileLoader<PrintFileRecord> printLoader,
                        FileLoader<SpoolFileRecord> spoolLoader,
                        FileLoader<PrinterFileRecord> printerLoader)
            throws IOException, ParseException {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.printManager = new PrintManager(printLoader);
        this.spoolManager = new SpoolManager(spoolLoader);
        this.printerManager = new PrinterManager(printerLoader, this.spoolManager);
        this.taskManager = new TaskManager();
        this.printingStrategies = List.of(getLessSpoolStrategy(), getEfficientSpoolUsage());
        this.printingStrategy = printingStrategies.get(0);
    }

    public void addListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void setPrintingStrategy(int printingStrategy) {
        switch (printingStrategy) {
            case 1 -> this.printingStrategy = this.printingStrategies.get(0);
            case 2 -> this.printingStrategy = this.printingStrategies.get(1);
        }
    }

    /**
     * Adds a print task to the printing queue.
     * @param printId the id of the new print task
     * @param colors the list of colors of the new print task
     * @param type tye filament type of the print task
     */
    public void addPrintTask(int printId, List<String> colors, int type) {
        try {
            Print print = getPrinterByIdHelper(printId);
            validateColors(colors, type);
            FilamentType ftype = getFilamentTypeHelper(type);

            this.taskManager.createPrintTask(print, colors, ftype);
            fireAnInstruction("Added task to queue");
        } catch (ValidationException e) {
            fireAnError(e.getMessage());
        }
    }

    /**
     * Starts the printing queue.
     */
    public void startInitialQueue() {
        /* TODO: This is extremely inefficient, because it will loop
         * through all printers even if there are no more print tasks
         * left in the queue.
         */
        for(Printer printer : this.printerManager.getPrinters()) {
            selectPrintTask(printer);
        }
    }

    /**
     * Gets all printers that have a currently assigned task.
     * @return a list of Strings in the format:
     * [printerId]: [printerName] - [printerCurrentTask]
     */
    public List<String> getRunningPrinters() {
        List<String> result = new ArrayList<>();
        for(Printer p : this.printerManager.getPrinters()) {
            PrintTask printerCurrentTask = this.taskManager.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                result.add(p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }

        return result;
    }

    /**
     * Indicates that a printer has failed printing a print task, and
     * updates the filament of the printer that tried to print the task
     * @param printerId the id of the failed printer
     */
    public void registerPrinterFailure(long printerId) {
        try {
            Map.Entry<Long, PrintTask> foundEntry = getPrintTaskEntry(printerId);
            PrintTask task = foundEntry.getValue();
            this.taskManager.addTaskToQueue(task);
            this.taskManager.removeRunningPrintTask(foundEntry.getKey());
            Printer printer = getPrinterById(foundEntry.getKey());

            fireAnInstruction("Task " + task + " removed from printer "
                    + printer.getName());

            FilamentReducingVisitor filamentReducingVisitor = new FilamentReducingVisitor(task);
            printer.accept(filamentReducingVisitor);

            selectPrintTask(printer);
        } catch (ValidationException e) {
            fireAnError(e.getMessage());
        }
    }

    /**
     * Indicates that a printer has completed printing a print task, and
     * updates the filament of the printer that tried to print the task
     * @param printerId the id of the printer that completed the task
     */
    public void registerCompletion(long printerId) {
        try {
            Map.Entry<Long, PrintTask> foundEntry = getPrintTaskEntry(printerId);
            PrintTask task = foundEntry.getValue();
            Printer printer = getPrinterById(printerId);

            fireAnInstruction("Task " + task + " removed from printer "
                    + printer.getName());

            FilamentReducingVisitor filamentReducingVisitor = new FilamentReducingVisitor(task);
            printer.accept(filamentReducingVisitor);

            selectPrintTask(printer);
        } catch (ValidationException e) {
            fireAnError(e.getMessage());
        } finally {
            this.taskManager.removeRunningPrintTask(printerId);
        }
    }

    /**
     * Gets all possible filaments for the printer system.
     * @return a list of Filament names as Strings
     */
    public List<String> getFilaments() {
        return Arrays.stream(FilamentType.values())
                .map(FilamentType::getName)
                .toList();
    }

    /**
     * Gets all prints available in the printer system.
     * @return a list of print names as Strings
     */
    public List<String> getPrintNames() {
        return this.printManager.getPrints().stream()
                .map(Print::getName)
                .toList();
    }

    /**
     * Gets all prints available in the printer system.
     * @return a list Strings containing full information about
     * every print
     */
    public List<String> getPrints() {
        return this.printManager.getPrints().stream()
                .map(Print::toString)
                .toList();
    }

    /**
     * Gets all spools available in the printer system.
     * @return a list Strings containing full information about
     * every spool
     */
    public List<String> getSpools() {
        return this.spoolManager.getSpools().stream()
                .map(Spool::toString)
                .toList();
    }

    /**
     * Gets all printers available in the printer system and their
     * current print task (if assigned).
     * @return a list Strings containing full information about
     * every printers
     */
    public List<String> getPrinters() {
        List<String> result = new ArrayList<>();
        for (var p : this.printerManager.getPrinters()) {
            StringBuilder printer = new StringBuilder(p.toString());
            PrintTask currentTask = this.taskManager.getPrinterCurrentTask(p);
            if (currentTask != null) {
                printer.append("Current Print Task: ")
                        .append(currentTask)
                        .append(System.lineSeparator());
            }
            result.add(printer.toString());
        }

        return result;
    }

    /**
     * Gets the amount of colors a print requires.
     * @param printChoice the printId
     * @return a number representing the amount of colors of that print
     */
    public int getPrintColorsSize(int printChoice) {
        return this.printManager.getPrints().get(printChoice).getFilamentLength().size();
    }

    /**
     * Gets all available colors for a certain filament.
     * @param filamentChoice the id of the filament
     * @return a list of Strings containing color names
     */
    public List<String> getAvailableColors(int filamentChoice) {
        List<String> result = new ArrayList<>();
        try {
            FilamentType filamentType = getFilamentTypeHelper(filamentChoice);

            result = this.spoolManager.getSpools().stream()
                    .filter(s -> s.getFilamentType() == filamentType)
                    .map(Spool::getColor)
                    .distinct()
                    .toList();
        } catch (ValidationException e) {
            fireAnError(e.getMessage());
        }

        return result;
    }

    /**
     * Gets all print tasks that are still waiting to be printed.
     * @return a list of Strings representing the whole information of
     * a print task.
     */
    public List<String> getPendingPrintTasks() {
        return this.taskManager.getPendingPrintTasks()
                .stream()
                .map(PrintTask::toString)
                .toList();
    }

    /**
     * Get the current printing strategy of the printer system.
     * @return a String representing the name of the strategy
     */
    public String getCurrentPrintingStrategy() {
        return this.printingStrategy.toString();
    }

    /**
     * Get all possible printing strategies of the printer system.
     * @return a list of Strings representing the name of the strategy
     */
    public List<String> getPrintingStrategies() {
        return this.printingStrategies.stream()
                .map(PrintingStrategy::toString)
                .toList();
    }

    /* HELPER METHODS */

    private void selectPrintTask(Printer printer) {
        Optional<PrintTask> chosenTaskOptional =
                this.printingStrategy.choosePrintTask(printer);

        if (chosenTaskOptional.isPresent()) {
            PrintTask chosenTask = chosenTaskOptional.get();
            this.taskManager.removePendingPrintTask(chosenTask);
            fireAnInstruction("Started task " + chosenTask + " on printer " + printer.getName());
        }
    }

    private PrintingStrategy getLessSpoolStrategy() {
        ChooseTaskVisitor chooseTaskVisitor = new ChooseTaskVisitor(
                this.taskManager.getPendingPrintTasks(),
                this.taskManager.getRunningPrintTasks(),
                this.printerManager.getFreePrinters()
        );

        SpoolSwitchingVisitor spoolSwitchingVisitor = new SpoolSwitchingVisitor(
                this.taskManager.getPendingPrintTasks(),
                this.taskManager.getRunningPrintTasks(),
                this.printerManager.getFreePrinters(),
                this.spoolManager.getFreeSpools(),
                this.propertyChangeSupport
        );

        return new LessSpoolChangeStrategy(chooseTaskVisitor, spoolSwitchingVisitor);
    }

    private PrintingStrategy getEfficientSpoolUsage() {
        EfficientSpoolVisitor efficientSpoolVisitor = new EfficientSpoolVisitor(
                this.propertyChangeSupport,
                this.taskManager.getPendingPrintTasks(),
                this.taskManager.getRunningPrintTasks(),
                this.printerManager.getFreePrinters(),
                this.spoolManager.getSpools(),
                this.spoolManager.getFreeSpools()
        );
        return new EfficientSpoolUsageStrategy(efficientSpoolVisitor);
    }

    private void fireAnError(String message) {
        this.propertyChangeSupport.firePropertyChange(
                "error",
                "",
                message
        );
    }

    private void fireAnInstruction(String message) {
        this.propertyChangeSupport.firePropertyChange(
                "instruction",
                "",
                message
        );
    }

    private void validateColors(List<String> colors, int type) throws ValidationException {
        if (colors.size() == 0) {
            throw new ValidationException("Need at least one color, but none given");
        }

        for (String color : colors) {
            this.spoolManager.getSpools().stream()
                    .filter(spool -> spool.getColor().equals(color) && spool.getFilamentType().getId() == type)
                    .findAny()
                    .orElseThrow(() -> new ValidationException("Color " + color + " (" + type +") not found"));
        }
    }

    private Map.Entry<Long, PrintTask> getPrintTaskEntry(long printerId)
            throws ValidationException {

        return this.taskManager.findEntryForPrinterId(printerId)
                .orElseThrow(() -> new ValidationException("cannot find a running task on printer with ID " + printerId));
    }

    private Printer getPrinterById(long printerId)
            throws ValidationException {

        return this.printerManager.getPrinterById(printerId)
                .orElseThrow(() -> new ValidationException("cannot find a printer with id " + printerId));
    }

    private Print getPrinterByIdHelper(int printId)
            throws ValidationException {

        return this.printManager.findPrintById(printId)
                .orElseThrow(() -> new ValidationException("Could not find print with name " + printId));
    }

    private FilamentType getFilamentTypeHelper(int type) throws ValidationException {
        return switch (type) {
            case 1 -> FilamentType.PLA;
            case 2 -> FilamentType.PETG;
            case 3 -> FilamentType.ABS;
            default -> throw new ValidationException("Not a valid filamentType, bailing out");
        };
    }
}
