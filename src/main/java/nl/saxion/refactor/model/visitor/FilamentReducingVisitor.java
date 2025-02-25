package nl.saxion.refactor.model.visitor;

import nl.saxion.refactor.model.FDMPrinter;
import nl.saxion.refactor.model.PrintTask;
import nl.saxion.refactor.model.SLAPrinter;
import nl.saxion.refactor.model.Spool;

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
