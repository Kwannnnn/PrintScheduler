package nl.saxion.model.newModel;

import nl.saxion.model.Print;

public interface Printer {
    String getName();
    boolean printFits(Print print);
    int calculatePrintTime(String filename);
    void accept(PrinterVisitor visitor);
}
