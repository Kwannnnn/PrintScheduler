package nl.saxion.model.factory;

import nl.saxion.model.FilamentType;
import nl.saxion.model.Spool;
import nl.saxion.model.Printer;
import nl.saxion.model.FDMPrinter;

import java.util.ArrayList;
import java.util.List;

public class PrinterFactory {
    public Printer createPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors, ArrayList<Spool> cspools) {
        Printer printer;
        switch (printerType) {
            case 1, 3 -> {
                printer = new FDMPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors,
                        List.of(FilamentType.PLA, FilamentType.PETG)
                );

                ((FDMPrinter) printer).setSpools(cspools);

            }
            case 2 -> {
                printer = new FDMPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors,
                        List.of(FilamentType.PLA, FilamentType.PETG, FilamentType.ABS)
                );

                ((FDMPrinter) printer).setSpools(cspools);

            }
            default -> throw new IllegalArgumentException("Invalid printer type '" + printerType + '"');
        }

        return printer;
    }
}
