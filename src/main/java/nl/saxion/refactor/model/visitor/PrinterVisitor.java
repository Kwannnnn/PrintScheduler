package nl.saxion.refactor.model.visitor;

import nl.saxion.refactor.model.FDMPrinter;
import nl.saxion.refactor.model.SLAPrinter;

public interface PrinterVisitor {
    void visit(FDMPrinter fdmPrinter);
    void visit(SLAPrinter slaPrinter);
}
