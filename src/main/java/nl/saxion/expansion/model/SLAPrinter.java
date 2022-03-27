package nl.saxion.expansion.model;

import nl.saxion.expansion.model.visitor.PrinterVisitor;

public class SLAPrinter implements Printer {
    @Override
    public Long getId() {
        return 0L;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean printFits(Print print) {
        return false;
    }

    @Override
    public int calculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public void accept(PrinterVisitor visitor) {

    }
}
