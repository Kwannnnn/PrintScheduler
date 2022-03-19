package nl.saxion.model.newModel;

import nl.saxion.model.FilamentType;
import nl.saxion.model.Print;
import nl.saxion.model.PrintTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final List<PrintTask> pendingPrintTasks;
    private final HashMap<Printer, PrintTask> runningPrintTasks;

    public TaskManager() {
        this.pendingPrintTasks = new ArrayList<>();
        this.runningPrintTasks = new HashMap<>();
    }

    public void addPrintTask(Print print, List<String> colors, FilamentType filamentType) {
        PrintTask task = new PrintTask(print, colors, filamentType);
        this.pendingPrintTasks.add(task);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return this.pendingPrintTasks;
    }

    public HashMap<Printer, PrintTask> getRunningPrintTasks() {
        return this.runningPrintTasks;
    }

    public void removePendingPrintTask(PrintTask task) {
        this.pendingPrintTasks.remove(task);
    }

    public void removeRunningPrintTask(Printer printer) {
        this.runningPrintTasks.remove(printer);
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!this.runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return this.runningPrintTasks.get(printer);
    }
}
