package nl.saxion.expansion.model.visitor;

import nl.saxion.expansion.model.*;

import java.beans.PropertyChangeSupport;
import java.util.*;

public class EfficientSpoolVisitor implements PrinterVisitor {
    private final PropertyChangeSupport support;
    private final List<PrintTask> pendingPrintTasks;
    private final HashMap<Long, PrintTask> runningPrintTasks;
    private final List<Printer> freePrinters;
    private final List<Spool> spools;
    private final List<Spool> freeSpools;
    private PrintTask chosenPrintTask;

    public EfficientSpoolVisitor(PropertyChangeSupport propertyChangeSupport,
                                 List<PrintTask> pendingPrintTasks,
                                 HashMap<Long, PrintTask> runningPrintTasks,
                                 List<Printer> freePrinters,
                                 List<Spool> spools,
                                 List<Spool> freeSpools) {
        this.support = propertyChangeSupport;
        this.pendingPrintTasks = pendingPrintTasks;
        this.runningPrintTasks = runningPrintTasks;
        this.freePrinters = freePrinters;
        this.spools = new ArrayList<>(spools);
        this.freeSpools = freeSpools;
    }

    public Optional<PrintTask> getChosenPrintTask() {
        return Optional.ofNullable(this.chosenPrintTask);
    }

    @Override
    public void visit(FDMPrinter fdmPrinter) {
        this.chosenPrintTask = null;

        sortSpoolsByLengthAsc();

        // Checks which print tasks from the print queue
        // can be printed by the printer
        List<PrintTask> printableTasks =
                getAllTasksPrintableByPrinter(fdmPrinter);

        // Sort the pending print tasks in descending order,
        // based on the sum of the filament lengths they require
        sortPrintTasksDesc(printableTasks);

        // Go through each spool, starting from the shortest
        // (spools list is sorted ascending)
        for (Spool spool : this.spools) {
            // If the spool is not free, or not already assigned to the
            // printer, skip it
            if (!this.freeSpools.contains(spool)
                    && isSpoolAssignedOnPrinter(fdmPrinter, spool)) {
                continue;
            }

            Optional<PrintTask> chosenTaskOptional =
                    findPrintTaskMatchingSpool(spool);

            if (chosenTaskOptional.isPresent()) {
                PrintTask chosenTask = chosenTaskOptional.get();
                this.runningPrintTasks.put(fdmPrinter.getId(), chosenTask);

                this.freeSpools.add(Arrays.asList(fdmPrinter.getSpools()).get(0));
                fdmPrinter.setSpools(List.of(spool));
                this.support.firePropertyChange(
                        "instruction",
                        "",
                        "Please place spool " + spool.getId() + " in printer " + fdmPrinter.getName());
                freeSpools.remove(spool);
                freePrinters.remove(fdmPrinter);
                this.chosenPrintTask = chosenTask;
                break;
            }
        }
    }

    @Override
    public void visit(SLAPrinter slaPrinter) {

    }

    /**
     * Sort the pending print tasks in descending order,
     * based on the sum of the filament lengths they require
     * @param printTasks
     */
    private void sortPrintTasksDesc(List<PrintTask> printTasks) {
        printTasks.sort((pt1, pt2) -> {
            int pt2Length = pt2.getPrint().getFilamentLength().stream()
                    .reduce(0, Integer::sum);

            int p1Length = pt1.getPrint().getFilamentLength().stream()
                    .reduce(0, Integer::sum);

            return pt2Length - p1Length;
        });
    }

    private void sortSpoolsByLengthAsc() {
        this.spools.sort(Comparator.comparing(Spool::getLength));
    }

    private boolean isSpoolAssignedOnPrinter(FDMPrinter fdmPrinter, Spool spool) {
        return !Arrays.asList(fdmPrinter.getSpools()).contains(spool);
    }

    private Optional<PrintTask> findPrintTaskMatchingSpool(Spool spool) {
        PrintTask chosenTask = null;
        for (PrintTask printTask : this.pendingPrintTasks) {
            for (int i = 0; i < printTask.getColors().size(); i++) {
                // Checks if the spool matches the color and the filament type of the print task
                if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                    chosenTask = printTask;
                }
            }
        }

        return Optional.ofNullable(chosenTask);
    }

    private List<PrintTask> getAllTasksPrintableByPrinter(FDMPrinter fdmPrinter) {
        List<PrintTask> result = new ArrayList<>();
        for (PrintTask printTask : this.pendingPrintTasks) {
            if (fdmPrinter.printFits(printTask.getPrint())
                    && fdmPrinter.getSupportedFilaments().contains(printTask.getFilamentType())) {
                result.add(printTask);
            }
        }

        return result;
    }
}
