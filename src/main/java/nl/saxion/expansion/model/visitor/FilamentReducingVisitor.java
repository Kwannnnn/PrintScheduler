package nl.saxion.expansion.model.visitor;

import nl.saxion.expansion.model.FDMPrinter;
import nl.saxion.expansion.model.PrintTask;
import nl.saxion.expansion.model.SLAPrinter;
import nl.saxion.expansion.model.Spool;

/**
 * A visitor that reduces the remaining filament of a Printer
 * based on the length required by the print task.
 */
public class FilamentReducingVisitor implements PrinterVisitor {
    private final PrintTask printTask;

    public FilamentReducingVisitor(PrintTask printTask) {
        this.printTask = printTask;
    }

    @Override
    public void visit(FDMPrinter fdmPrinter) {
        Spool[] spools = fdmPrinter.getSpools();
        for(int i = 0; i < spools.length && i < this.printTask.getColors().size(); i++) {
            spools[i].reduceLength(this.printTask.getPrint().getFilamentLength().get(i));
        }
    }

    @Override
    public void visit(SLAPrinter slaPrinter) {

    }
}
