package nl.saxion.expansion.model.visitor;

import nl.saxion.expansion.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * ChooseTaskVisitor finds a suitable task for a printer, that matches
 * the currently assigned spools on that particular printer.
 */
public class ChooseTaskVisitor implements PrinterVisitor {
    private final List<PrintTask> pendingPrintTasks;
    private final HashMap<Long, PrintTask> runningPrintTasks;
    private final List<Printer> freePrinters;
    private PrintTask chosenPrintTask;

    public ChooseTaskVisitor(List<PrintTask> pendingPrintTasks,
                             HashMap<Long, PrintTask> runningPrintTasks,
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
        this.chosenPrintTask = null;

        // First we look if there's a task that matches the current spool on the printer.
        for(PrintTask printTask : pendingPrintTasks) {
            // Check if print in printTask fits into printer if the printer supports the
            // filament type of this particular PrintTask
            if(!fdmPrinter.printFits(printTask.getPrint())
                    || !fdmPrinter.supportsFilament(printTask.getFilamentType())
                    || !(printTask.getColors().size() <= fdmPrinter.getMaxSpools())) {
                continue;
            }

            if (fdmPrinterCanPrint(fdmPrinter, printTask)) {
                // Change the status of the task to running
                this.runningPrintTasks.put(fdmPrinter.getId(), printTask);
                // Change the status of the printer to busy
                this.freePrinters.remove(fdmPrinter);

                this.chosenPrintTask = printTask;
                return;
            }
        }
    }

    @Override
    public void visit(SLAPrinter slaPrinter) {

    }

    /**
     * Checks whether a printer has the spools required for the print task
     * already attached on itself.
     * @param fdmPrinter the FDMPrinter
     * @param printTask The PrintTask
     * @return true if all spools required for the print task are attached on
     * the printer
     */
    private boolean fdmPrinterCanPrint(FDMPrinter fdmPrinter, PrintTask printTask) {
        boolean printWorks = true;
        Spool[] spools = fdmPrinter.getSpools();

        for (int i = 0; i < fdmPrinter.getSpools().length && i < printTask.getColors().size(); i++) {
            if (spools[i] == null || !spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                printWorks = false;
            }
        }

        return printWorks;
    }
}
