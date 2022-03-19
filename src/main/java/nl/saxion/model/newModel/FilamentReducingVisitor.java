package nl.saxion.model.newModel;

import nl.saxion.model.PrintTask;
import nl.saxion.model.Spool;

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
