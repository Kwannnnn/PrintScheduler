package nl.saxion.model.newModel;

public interface PrinterVisitor {
    void visit(FDMPrinter fdmPrinter);
    void visit(SLAPrinter slaPrinter);
}
