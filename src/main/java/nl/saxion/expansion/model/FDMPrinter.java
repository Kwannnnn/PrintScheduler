package nl.saxion.expansion.model;

import nl.saxion.expansion.model.visitor.PrinterVisitor;

import java.util.List;

public class FDMPrinter implements Printer {
    private final Long id;
    private final String name;
    private final String manufacturer;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final int maxSpools;
    private final Spool[] spools;
    private final List<FilamentType> supportedFilaments;

    public FDMPrinter(long id, String name, String manufacturer, int maxX, int maxY, int maxZ, int maxSpools, List<FilamentType> supportedFilaments) {
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

    public int getMaxSpools() {
        return this.maxSpools;
    }

    public Spool[] getSpools() {
        return this.spools;
    }

    public List<FilamentType> getSupportedFilaments() {
        return this.supportedFilaments;
    }

    public boolean supportsFilament(FilamentType filamentType) {
        return this.supportedFilaments.contains(filamentType);
    }

    public void setSpools(List<Spool> spools) {
        for (int i = 0; i < spools.size() && i < this.maxSpools; i++) {
            this.spools[i] = spools.get(i);
        }
    }

    @Override
    public Long getId() {
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
    public int calculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public void accept(PrinterVisitor visitor) {
        visitor.visit(this);
    }

    private int getSpoolId(Spool spool) {
        return spool != null
                ? spool.getId()
                : -1;
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
                        "Current spool: " + this.getSpoolId(this.spools[0]) + System.lineSeparator());
        if(maxSpools > 1) {
            toPrint.append("maxColors: ").append(maxSpools).append(System.lineSeparator());
            for (int i = 1; i < this.spools.length; i++) {
                toPrint.append("spool")
                        .append(i + 1)
                        .append(": ")
                        .append(getSpoolId(this.spools[i]))
                        .append(System.lineSeparator());
            }
        }
        return toPrint.toString();
    }
}
