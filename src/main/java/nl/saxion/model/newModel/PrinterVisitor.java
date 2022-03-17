package nl.saxion.model.newModel;

public interface PrinterVisitor {
    void visit(FDMPrinter standardFDMPrinter);
    void visit(SLAPrinter slaPrinter);
}
