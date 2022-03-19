package nl.saxion.refactor.model;

import nl.saxion.refactor.model.visitor.PrinterVisitor;

public class SLAPrinter implements Printer {
    @Override
    public int getId() {
        return 0;
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
