package nl.saxion.expansion.model.visitor;

import nl.saxion.expansion.model.*;

import java.beans.PropertyChangeSupport;
import java.util.*;

public class SpoolSwitchingVisitor implements PrinterVisitor {
    private final List<PrintTask> pendingPrintTasks;
    private final HashMap<Long, PrintTask> runningPrintTasks;
    private final List<Printer> freePrinters;
    private final List<Spool> freeSpools;
    private final PropertyChangeSupport support;
    private PrintTask chosenPrintTask;

    public SpoolSwitchingVisitor(List<PrintTask> pendingPrintTasks,
                                 HashMap<Long, PrintTask> runningPrintTasks,
                                 List<Printer> freePrinters,
                                 List<Spool> freeSpools,
                                 PropertyChangeSupport support) {
        this.pendingPrintTasks = pendingPrintTasks;
        this.runningPrintTasks = runningPrintTasks;
        this.freePrinters = freePrinters;
        this.freeSpools = freeSpools;
        this.support = support;
    }

    public Optional<PrintTask> getChosenPrintTask() {
        return Optional.ofNullable(this.chosenPrintTask);
    }

    @Override
    public void visit(FDMPrinter fdmPrinter) {
        this.chosenPrintTask = null;
        // If we didn't find a print for the current spool we search for a print with the free spools.
        for(PrintTask printTask : pendingPrintTasks) {
            // FIXME: There was a bug with the original code
            if (!this.freePrinters.contains(fdmPrinter)) {
                return;
            }

            if(fdmPrinter.printFits(printTask.getPrint())
                    && fdmPrinter.getSupportedFilaments().contains(printTask.getFilamentType())) {
                ArrayList<Spool> chosenSpools = new ArrayList<>();
                for (int i = 0; i < fdmPrinter.getMaxSpools() && i < printTask.getColors().size(); i++) {
                    for (Spool spool : freeSpools) {
                        // Checks if the spool matches the color and the filament type of the print task
                        // And if ...
                        if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())
                                && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
                            chosenSpools.add(spool);
                        }
                    }
                }

                // We assume that if they are the same length that there is a match.
                if (chosenSpools.size() == printTask.getColors().size()) {
                    runningPrintTasks.put(fdmPrinter.getId(), printTask);
                    freeSpools.addAll(Arrays.asList(fdmPrinter.getSpools()));
                    fdmPrinter.setSpools(chosenSpools);

                    for (Spool spool : chosenSpools) {
                        this.support.firePropertyChange(
                                "instruction",
                                "",
                                "Please place spool " + spool.getId() + " in printer " + fdmPrinter.getName());
                        freeSpools.remove(spool);
                    }
                    freePrinters.remove(fdmPrinter);
                    this.chosenPrintTask = printTask;
                }
            }
        }
    }

    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    @Override
    public void visit(SLAPrinter slaPrinter) {

    }
}
