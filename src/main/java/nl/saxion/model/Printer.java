package nl.saxion.model;

import nl.saxion.model.visitor.PrinterVisitor;

public interface Printer {
    int getId();
    String getName();
    boolean printFits(Print print);
    int calculatePrintTime(String filename);
    void accept(PrinterVisitor visitor);
}
