package nl.saxion.expansion.model.visitor;

import nl.saxion.expansion.model.FDMPrinter;
import nl.saxion.expansion.model.SLAPrinter;

public interface PrinterVisitor {
    void visit(FDMPrinter fdmPrinter);
    void visit(SLAPrinter slaPrinter);
}
