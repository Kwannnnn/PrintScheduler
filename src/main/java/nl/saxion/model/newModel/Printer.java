package nl.saxion.model.newModel;

import nl.saxion.model.Print;

public interface Printer {
    int getId();
    String getName();
    boolean printFits(Print print);
    int calculatePrintTime(String filename);
    void accept(PrinterVisitor visitor);
}
