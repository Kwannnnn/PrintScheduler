package nl.saxion.model.newModel;

import nl.saxion.model.FilamentType;
import nl.saxion.model.Print;
import nl.saxion.model.Spool;

import java.util.List;

public class StandardFDMPrinter implements FDMPrinter {
    private final int id;
    private final String name;
    private final String manufacturer;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final int maxSpools;
    private final Spool[] spools;
    private final List<FilamentType> supportedFilaments;

    public StandardFDMPrinter(int id, String name, String manufacturer, int maxX, int maxY, int maxZ, int maxSpools, List<FilamentType> supportedFilaments) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.maxSpools = maxSpools < 0 ? 1 : maxSpools;
        this.supportedFilaments = supportedFilaments;
        this.spools = new Spool[this.maxSpools];
    }

    @Override
    public int getMaxSpools() {
        return this.maxSpools;
    }

    @Override
    public Spool[] getSpools() {
        return this.spools;
    }

    @Override
    public void setSpools(List<Spool> spools) {
        for (int i = 0; i < spools.size() && i < this.maxSpools; i++) {
            this.spools[i] = spools.get(i);
        }
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean printFits(Print print) {
        return print.getHeight() <= maxZ && print.getWidth() <= maxX && print.getLength() <= maxY;
    }

    @Override
    public List<FilamentType> getSupportedFilaments() {
        return this.supportedFilaments;
    }

    @Override
    public int calculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public void accept(PrinterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder toPrint =
                new StringBuilder("ID: " + id + System.lineSeparator() +
                        "Name: " + name + System.lineSeparator() +
                        "Manufacturer: " + manufacturer + System.lineSeparator() +
                        "maxX: " + maxX + System.lineSeparator() +
                        "maxY: " + maxY + System.lineSeparator() +
                        "maxZ: " + maxZ + System.lineSeparator() +
                        "Current spool: " + spools[0].getId() + System.lineSeparator());
        if(maxSpools > 1) {
            toPrint.append("maxColors: ").append(maxSpools).append(System.lineSeparator());
            for (int i = 1; i < this.spools.length; i++) {
                toPrint.append("spool")
                        .append(i + 1)
                        .append(": ")
                        .append(this.spools[i].getId())
                        .append(System.lineSeparator());
            }
        }
        return toPrint.toString();
    }
}
