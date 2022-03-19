package nl.saxion.io;

import nl.saxion.model.FilamentType;
import nl.saxion.model.Spool;
import nl.saxion.model.newModel.Printer;
import nl.saxion.model.newModel.StandardFDMPrinter;

import java.util.ArrayList;
import java.util.List;

public class PrinterFactory {
    public Printer createPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors, ArrayList<Spool> cspools) {
        Printer printer;
        switch (printerType) {
            case 1, 3 -> {
                printer = new StandardFDMPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors,
                        List.of(FilamentType.PLA, FilamentType.PETG)
                );

                ((StandardFDMPrinter) printer).setSpools(cspools);

            }
            case 2 -> {
                printer = new StandardFDMPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors,
                        List.of(FilamentType.PLA, FilamentType.PETG, FilamentType.ABS)
                );

                ((StandardFDMPrinter) printer).setSpools(cspools);

            }
            default -> throw new IllegalArgumentException("Invalid printer type '" + printerType + '"');
        }

        return printer;
    }
}
