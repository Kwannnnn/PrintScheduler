package nl.saxion.model.visitor;

import nl.saxion.model.FDMPrinter;
import nl.saxion.model.SLAPrinter;

public interface PrinterVisitor {
    void visit(FDMPrinter fdmPrinter);
    void visit(SLAPrinter slaPrinter);
}
