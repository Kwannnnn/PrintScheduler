package nl.saxion.model.newModel;

import nl.saxion.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ChooseTaskVisitor implements PrinterVisitor {
    private final List<PrintTask> pendingPrintTasks;
    private final HashMap<Printer, PrintTask> runningPrintTasks;
    private final List<Printer> freePrinters;
    private PrintTask chosenPrintTask;

    public ChooseTaskVisitor(List<PrintTask> pendingPrintTasks,
                             HashMap<Printer, PrintTask> runningPrintTasks,
                             List<Printer> freePrinters) {
        this.pendingPrintTasks = pendingPrintTasks;
        this.runningPrintTasks = runningPrintTasks;
        this.freePrinters = freePrinters;
    }

    public Optional<PrintTask> getChosenPrintTask() {
        return Optional.ofNullable(this.chosenPrintTask);
    }

    @Override
    public void visit(FDMPrinter fdmPrinter) {
        Spool[] spools = fdmPrinter.getSpools();

        // First we look if there's a task that matches the current spool on the printer.
        for(PrintTask printTask : pendingPrintTasks) {
            // Check if print in printTask fits into printer if the printer supports the
            // filament type of this particular PrintTask
            if(fdmPrinter.printFits(printTask.getPrint())
                    && fdmPrinter.getSupportedFilaments().contains(printTask.getFilamentType())
                    && printTask.getColors().size() <= fdmPrinter.getMaxSpools()) {
                boolean printWorks = true;
                for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
                    if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                        printWorks = false;
                    }
                }
                if (printWorks) {
                    runningPrintTasks.put(fdmPrinter, printTask);
                    freePrinters.remove(fdmPrinter);
                    this.chosenPrintTask = printTask;
                    break;
                }
            }
        }
    }

    @Override
    public void visit(SLAPrinter slaPrinter) {

    }
}
