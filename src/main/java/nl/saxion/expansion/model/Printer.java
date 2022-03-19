package nl.saxion.expansion.model;

import nl.saxion.expansion.model.visitor.PrinterVisitor;

public interface Printer {
    int getId();
    String getName();
    boolean printFits(Print print);
    int calculatePrintTime(String filename);
    void accept(PrinterVisitor visitor);
}
