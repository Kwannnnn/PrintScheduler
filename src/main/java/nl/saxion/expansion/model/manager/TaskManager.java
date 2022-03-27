package nl.saxion.expansion.model.manager;

import nl.saxion.expansion.model.FilamentType;
import nl.saxion.expansion.model.Print;
import nl.saxion.expansion.model.PrintTask;
import nl.saxion.expansion.model.Printer;

import java.util.*;

public class TaskManager {
    private final List<PrintTask> pendingPrintTasks;
    private final HashMap<Long, PrintTask> runningPrintTasks;

    public TaskManager() {
        this.pendingPrintTasks = new ArrayList<>();
        this.runningPrintTasks = new HashMap<>();
    }

    public void createPrintTask(Print print, List<String> colors, FilamentType filamentType) {
        PrintTask task = new PrintTask(print, colors, filamentType);
        this.pendingPrintTasks.add(task);
    }

    public void addTaskToQueue(PrintTask task) {
        this.pendingPrintTasks.add(task);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return this.pendingPrintTasks;
    }

    public HashMap<Long, PrintTask> getRunningPrintTasks() {
        return this.runningPrintTasks;
    }

    public Optional<Map.Entry<Long, PrintTask>> findEntryForPrinterId(Long printerId) {
        Map.Entry<Long, PrintTask> foundEntry = null;
        for (Map.Entry<Long, PrintTask> entry : this.runningPrintTasks.entrySet()) {
            if (entry.getKey().equals(printerId)) {
                foundEntry = entry;
                break;
            }
        }

        return Optional.ofNullable(foundEntry);
    }

    public void removePendingPrintTask(PrintTask task) {
        this.pendingPrintTasks.remove(task);
    }

    public void removeRunningPrintTask(Long printerId) {
        this.runningPrintTasks.remove(printerId);
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!this.runningPrintTasks.containsKey(printer.getId())) {
            return null;
        }
        return this.runningPrintTasks.get(printer.getId());
    }
}
