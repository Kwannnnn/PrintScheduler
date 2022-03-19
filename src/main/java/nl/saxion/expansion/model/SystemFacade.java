package nl.saxion.expansion.model;

import nl.saxion.expansion.model.manager.PrintManager;
import nl.saxion.expansion.model.manager.PrinterManager;
import nl.saxion.expansion.model.manager.SpoolManager;
import nl.saxion.expansion.model.manager.TaskManager;
import nl.saxion.expansion.model.visitor.ChooseTaskVisitor;
import nl.saxion.expansion.model.visitor.FilamentReducingVisitor;
import nl.saxion.expansion.model.visitor.SpoolSwitchingVisitor;
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

    public SystemFacade() throws IOException, ParseException {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.printManager = new PrintManager();
        this.spoolManager = new SpoolManager();
        this.printerManager = new PrinterManager(this.spoolManager);
        this.taskManager = new TaskManager();
    }

    public void addListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPrintTask(int printId, List<String> colors, int type) {
        Print print = this.printManager.findPrintById(printId);
        if (print == null) {
            // TODO: printId ???
            fireAnError("Could not find print with name " + printId);
            return;
        }

        if (colors.size() == 0) {
            fireAnError("Need at least one color, but none given");
            return;
        }

        for (String color : colors) {
            boolean found = false;
            for (Spool spool : this.spoolManager.getSpools()) {
                if (spool.getColor().equals(color) && spool.getFilamentType().getId() == type) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                fireAnError("Color " + color + " (" + type +") not found");
                return;
            }
        }

        FilamentType ftype = switch (type) {
            case 1 -> FilamentType.PLA;
            case 2 -> FilamentType.PETG;
            case 3 -> FilamentType.ABS;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        this.taskManager.addPrintTask(print, colors, ftype);
        fireAnInstruction("Added task to queue");
    }

    public void startInitialQueue() {
        /* TODO: This is extremely inefficient, because it will loop
         * through all printers even if there are no more print tasks
         * left in the queue.
         */
        for(Printer printer : this.printerManager.getPrinters()) {
            selectPrintTask(printer);
        }
    }

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

    public void registerPrinterFailure(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : this.taskManager.getRunningPrintTasks().entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            fireAnError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        // TODO: this is not good
        this.taskManager.getPendingPrintTasks().add(task); // add the task back to the queue.
        this.taskManager.removeRunningPrintTask(foundEntry.getKey());

        fireAnInstruction("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        FilamentReducingVisitor filamentReducingVisitor = new FilamentReducingVisitor(task);
        printer.accept(filamentReducingVisitor);
        selectPrintTask(printer);
    }

    public void registerCompletion(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : this.taskManager.getRunningPrintTasks().entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            fireAnError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        this.taskManager.removeRunningPrintTask(foundEntry.getKey());

        fireAnInstruction("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        FilamentReducingVisitor filamentReducingVisitor = new FilamentReducingVisitor(task);
        printer.accept(filamentReducingVisitor);

        selectPrintTask(printer);
    }

    public List<String> getFilaments() {
        return Arrays.stream(FilamentType.values())
                .map(FilamentType::getName)
                .toList();
    }

    public List<String> getPrintNames() {
        return this.printManager.getPrints().stream()
                .map(Print::getName)
                .toList();
    }

    public List<String> getPrints() {
        return this.printManager.getPrints().stream()
                .map(Print::toString)
                .toList();
    }

    public List<String> getSpools() {
        return this.spoolManager.getSpools().stream()
                .map(Spool::toString)
                .toList();
    }

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

    public int getPrintColorsSize(int printChoice) {
        return this.printManager.getPrints().get(printChoice).getFilamentLength().size();
    }

    public List<String> getAvailableColors(int filamentChoice) {
        FilamentType filamentType = Arrays
                .stream(FilamentType.values())
                .filter(f -> f.getId() == filamentChoice)
                .findFirst()
                .orElse(null);

        assert filamentType != null;

        return this.spoolManager.getSpools().stream()
                .filter(s -> s.getFilamentType() == filamentType)
                .map(Spool::getColor)
                .distinct()
                .toList();
    }

    public List<String> getPendingPrintTasks() {
        return this.taskManager.getPendingPrintTasks()
                .stream()
                .map(PrintTask::toString)
                .toList();
    }

    private void selectPrintTask(Printer printer) {
        Optional<PrintTask> chosenTaskOptional =
                chooseTaskUsingCurrentSpools(printer)
                        .or(() -> chooseTaskUsingFreeSpools(printer));

        if (chosenTaskOptional.isPresent()) {
            PrintTask chosenTask = chosenTaskOptional.get();
            this.taskManager.removePendingPrintTask(chosenTask);
            fireAnInstruction("Started task " + chosenTask + " on printer " + printer.getName());
        }
    }

    private Optional<PrintTask> chooseTaskUsingCurrentSpools(Printer printer) {
        ChooseTaskVisitor chooseTaskVisitor = new ChooseTaskVisitor(
                this.taskManager.getPendingPrintTasks(),
                this.taskManager.getRunningPrintTasks(),
                this.printerManager.getFreePrinters());
        printer.accept(chooseTaskVisitor);

        return chooseTaskVisitor.getChosenPrintTask();
    }

    private Optional<PrintTask> chooseTaskUsingFreeSpools(Printer printer) {
        SpoolSwitchingVisitor spoolSwitchingVisitor = new SpoolSwitchingVisitor(
                this.taskManager.getPendingPrintTasks(),
                this.taskManager.getRunningPrintTasks(),
                this.printerManager.getFreePrinters(),
                this.spoolManager.getFreeSpools(),
                this.propertyChangeSupport);
        printer.accept(spoolSwitchingVisitor);

        return spoolSwitchingVisitor.getChosenPrintTask();
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
}
